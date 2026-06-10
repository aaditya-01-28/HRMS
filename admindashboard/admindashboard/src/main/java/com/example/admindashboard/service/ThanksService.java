package com.example.admindashboard.service;

import com.example.admindashboard.model.ThanksNotification;
import com.example.admindashboard.model.ThanksTransaction;
import com.example.admindashboard.model.ThanksWallet;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.ThanksNotificationRepository;
import com.example.admindashboard.repository.ThanksTransactionRepository;
import com.example.admindashboard.repository.ThanksWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ThanksService {

    @Autowired
    private ThanksWalletRepository walletRepository;

    @Autowired
    private ThanksTransactionRepository transactionRepository;

    @Autowired
    private ThanksNotificationRepository notificationRepository;

    /**
     * Gets the user's wallet. If they don't have one, it creates one
     * and gives them a 1000 PTS welcome bonus so they can test the store!
     */
    public ThanksWallet getOrCreateWallet(User user) {
        return walletRepository.findByUser(user).orElseGet(() -> {
            ThanksWallet newWallet = new ThanksWallet(user);
            newWallet.setTotalPointsEarned(1000);
            newWallet.setWalletBalance(1000);
            newWallet.setRewardsReceived(0);

            // Log the welcome bonus transaction
            ThanksTransaction bonusTx = new ThanksTransaction();
            bonusTx.setUser(user);
            bonusTx.setTransactionDate(LocalDate.now());
            bonusTx.setTransactionType("Credit");
            bonusTx.setPoints(1000);
            bonusTx.setCategory("System");
            bonusTx.setDescription("Welcome Bonus Points");
            transactionRepository.save(bonusTx);

            return walletRepository.save(newWallet);
        });
    }

    /**
     * Handles the "Send Thanks" peer-to-peer point transfer.
     */
    @Transactional
    public void sendThanks(User sender, User receiver, Integer points, String category, String message) throws Exception {
        ThanksWallet senderWallet = getOrCreateWallet(sender);
        ThanksWallet receiverWallet = getOrCreateWallet(receiver);

        // 1. Verify Sender has enough points
        if (senderWallet.getWalletBalance() < points) {
            throw new Exception("Insufficient points balance.");
        }

        // 2. Deduct from Sender
        senderWallet.setWalletBalance(senderWallet.getWalletBalance() - points);
        walletRepository.save(senderWallet);

        // 3. Log Sender Transaction
        ThanksTransaction senderTx = new ThanksTransaction();
        senderTx.setUser(sender);
        senderTx.setTransactionDate(LocalDate.now());
        senderTx.setTransactionType("Debit");
        senderTx.setPoints(points);
        senderTx.setCategory(category);
        senderTx.setDescription("Appreciation sent to " + receiver.getFullName());
        transactionRepository.save(senderTx);

        // Notification for sender
        notificationRepository.save(new ThanksNotification(
            sender,
            "Points Sent",
            "You sent " + points + " PTS to " + receiver.getFullName() + " as appreciation.",
            "DEBIT"
        ));

        // 4. Add to Receiver
        receiverWallet.setWalletBalance(receiverWallet.getWalletBalance() + points);
        receiverWallet.setTotalPointsEarned(receiverWallet.getTotalPointsEarned() + points);
        receiverWallet.setRewardsReceived(receiverWallet.getRewardsReceived() + 1);
        walletRepository.save(receiverWallet);

        // 5. Log Receiver Transaction
        ThanksTransaction receiverTx = new ThanksTransaction();
        receiverTx.setUser(receiver);
        receiverTx.setTransactionDate(LocalDate.now());
        receiverTx.setTransactionType("Credit");
        receiverTx.setPoints(points);
        receiverTx.setCategory(category);
        receiverTx.setDescription("Appreciation from " + sender.getFullName() + ": " + message);
        transactionRepository.save(receiverTx);

        // Notification for receiver
        notificationRepository.save(new ThanksNotification(
            receiver,
            "Points Received",
            "You received " + points + " PTS from " + sender.getFullName() + " — \"" + message + "\"",
            "CREDIT"
        ));
    }

    /**
     * Handles redeeming a product from the Store.
     */
    @Transactional
    public void redeemItem(User user, String itemName, Integer points, String productType) throws Exception {
        ThanksWallet wallet = getOrCreateWallet(user);

        // 1. Verify Balance
        if (wallet.getWalletBalance() < points) {
            throw new Exception("Insufficient points for this redemption.");
        }

        // 2. Deduct Points
        wallet.setWalletBalance(wallet.getWalletBalance() - points);
        walletRepository.save(wallet);

        // 3. Log the Store Purchase
        ThanksTransaction tx = new ThanksTransaction();
        tx.setUser(user);
        tx.setTransactionDate(LocalDate.now());
        tx.setTransactionType("Debit");
        tx.setPoints(points);
        tx.setCategory("Store Redemption");

        String desc = productType.equals("digital") ? "Redeemed Digital Gift Card: " : "Redeemed Merchandise: ";
        tx.setDescription(desc + itemName);

        transactionRepository.save(tx);

        // Notification for order placed
        notificationRepository.save(new ThanksNotification(
            user,
            "Order Placed",
            "You redeemed \"" + itemName + "\" for " + points + " PTS. " +
                (productType.equals("merch") ? "It will be delivered within 5 business days." : "The voucher will be sent to your email."),
            "ORDER"
        ));
    }

    /**
     * Fetches the user's notifications.
     */
    public List<ThanksNotification> getNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Returns count of unread notifications.
     */
    public int getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    /**
     * Marks all notifications as read.
     */
    @Transactional
    public void markAllNotificationsRead(User user) {
        List<ThanksNotification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        for (ThanksNotification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }

    /**
     * Fetches the user's transaction history.
     */
    public List<ThanksTransaction> getTransactionHistory(User user) {
        return transactionRepository.findByUserOrderByTransactionDateDesc(user);
    }

    /**
     * Fetches the user's transaction history with filtering.
     */
    public List<ThanksTransaction> getFilteredTransactionHistory(User user, LocalDate startDate, LocalDate endDate, String searchQuery) {
        if (searchQuery != null && searchQuery.trim().isEmpty()) {
            searchQuery = null;
        } else if (searchQuery != null) {
            searchQuery = "%" + searchQuery.toLowerCase() + "%";
        }
        return transactionRepository.findFilteredTransactions(user, startDate, endDate, searchQuery);
    }
}