package com.amplifiers.pathfinder.entity.transaction;

import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final UserUtility userUtility;

    public Optional<Transaction> findByTranxId(String tranxId) {
        Optional<Transaction> transaction = repository.findByTranxId(tranxId);

        if (transaction.isEmpty()) {
            return Optional.empty();
        }

        // make sure only the buyer can access a transaction
        User currentUser = userUtility.getCurrentUser();
        User buyer = transaction.get().getEnrollment().getBuyer();

        if (!Objects.equals(currentUser.getId(), buyer.getId())) {
            return Optional.empty();
        }

        return transaction;
    }
}
