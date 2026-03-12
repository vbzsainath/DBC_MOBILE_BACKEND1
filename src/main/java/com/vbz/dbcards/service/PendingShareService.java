package com.vbz.dbcards.service;

import com.vbz.dbcards.entity.*;
import com.vbz.dbcards.utils.ShareTokenUtil;
import com.vbz.dbcards.enums.ShareStatus;
import com.vbz.dbcards.exception.ResourceNotFoundException;
import com.vbz.dbcards.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PendingShareService {

    private final PendingShareRepository pendingShareRepository;
    private final DbcShareInfoRepository shareRepository;
    private final BusinessCardRepository cardRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SmsService smsService;

    @Value("${app.base-url:https://sharecards.in}")
    private String appBaseUrl;


    @Transactional
    public Map<String, String> generateInviteAndLink(
            Long senderId,
            Long cardId,
            String receiverName,
            Long receiverMobile,
            String receiverEmail) {

        log.info("Generate invite: sender={}, card={}, receiverMobile={}",
                senderId, cardId, receiverMobile);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sender not found with id: " + senderId));

        BusinessCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Card not found with id: " + cardId));


        boolean alreadyInvited = pendingShareRepository
                .existsBySenderIdAndReceiverMobileAndCardId(
                        senderId, receiverMobile, cardId);

        if (!alreadyInvited) {

            PendingShare pending = new PendingShare();
            pending.setSenderId(senderId);
            pending.setCardId(cardId);
            pending.setReceiverMobile(receiverMobile);
            pending.setReceiverName(receiverName);
            pending.setReceiverEmail(receiverEmail);

            pendingShareRepository.save(pending);

            log.info("PendingShare saved: sender={}, card={}, mobile={}",
                    senderId, cardId, receiverMobile);
        }


        String token = ShareTokenUtil.generateToken(cardId, receiverMobile);

     // Generate plain text share link
        String shareLink = "https://card.sharecards.in/" + cardId;

        String senderFullName = buildFullName(sender);

        String greeting = receiverName != null && !receiverName.isBlank()
                ? "Hi " + receiverName + ","
                : "Hello,";


        String inviteBody =
                greeting + "\n\n"
                        + "You have received a Digital Business Card.\n\n"
                        + "View here:\n"
                        + shareLink + "\n\n"
                        + "Sign up on ShareCards to unlock more features and manage your digital cards.";


        String emailSubject = senderFullName + " shared a Digital Business Card with you";


        try {

            smsService.sendInviteSms(
                    receiverMobile,
                    inviteBody
            );

            log.info("Invite SMS sent to {}", receiverMobile);

        } catch (Exception e) {

            log.warn("Invite SMS failed for {}: {}", receiverMobile, e.getMessage());

        }


        if (receiverEmail != null && !receiverEmail.isBlank()) {

            try {

                emailService.sendInviteEmail(receiverEmail, emailSubject, inviteBody);

                log.info("Invite email sent to {}", receiverEmail);

            } catch (Exception e) {

                log.warn("Invite email failed for {}: {}", receiverEmail, e.getMessage());

            }

        }


        String whatsappText =
                greeting + "\n\n"
                        + "You have received a Digital Business Card.\n\n"
                        + "View here:\n"
                        + shareLink + "\n\n"
                        + "Sign up on ShareCards to unlock more features and manage your digital cards.";


        String whatsappUrl = "https://wa.me/91" + receiverMobile
                + "?text=" + java.net.URLEncoder.encode(
                whatsappText, java.nio.charset.StandardCharsets.UTF_8);


        Map<String, String> result = new HashMap<>();

        result.put("shareLink", shareLink);
        result.put("whatsappUrl", whatsappUrl);
        result.put("smsBody", inviteBody);
        result.put("emailSubject", emailSubject);
        result.put("emailBody", inviteBody);

        return result;
    }


    @Transactional
    public void claimPendingShares(Long userId, Long mobileNumber) {

        List<PendingShare> pending =
                pendingShareRepository.findByReceiverMobileAndClaimedFalse(mobileNumber);

        if (pending.isEmpty()) {
            log.info("No pending shares to claim for mobile={}", mobileNumber);
            return;
        }

        log.info("Claiming {} pending share(s) for userId={}, mobile={}",
                pending.size(), userId, mobileNumber);


        for (PendingShare ps : pending) {

            try {

                boolean cardExists = cardRepository.existsById(ps.getCardId());

                if (!cardExists) {

                    ps.setClaimed(true);
                    pendingShareRepository.save(ps);
                    continue;
                }


                boolean alreadyExists = shareRepository
                        .existsBySharedByUseridAndSharedToUseridAndSharedDbcIdAndStatuscodeTrue(
                                ps.getSenderId(), userId, ps.getCardId());

                if (alreadyExists) {

                    ps.setClaimed(true);
                    pendingShareRepository.save(ps);
                    continue;
                }


                DbcShareInfo share = shareRepository
                        .findBySharedByUseridAndSharedToUseridAndSharedDbcId(
                                ps.getSenderId(), userId, ps.getCardId())
                        .orElse(new DbcShareInfo());


                share.setSharedByUserid(ps.getSenderId());
                share.setSharedToUserid(userId);
                share.setSharedDbcId(ps.getCardId());
                share.setStatuscode(true);
                share.setShareStatus(ShareStatus.SENT);

                shareRepository.save(share);


                notificationService.sendNotification(
                        ps.getSenderId(),
                        userId,
                        "You have a pending business card waiting in your inbox"
                );


                ps.setClaimed(true);
                pendingShareRepository.save(ps);

            } catch (Exception e) {

                log.error("Failed to claim pendingShareId={}: {}", ps.getId(), e.getMessage());

            }

        }

    }


    private String buildFullName(User user) {

        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String middle = user.getMiddleName() != null ? " " + user.getMiddleName() : "";
        String last = user.getLastName() != null ? " " + user.getLastName() : "";

        String full = (first + middle + last).trim();

        return full.isBlank()
                ? (user.getName() != null ? user.getName() : "A DBC User")
                : full;
    }

}