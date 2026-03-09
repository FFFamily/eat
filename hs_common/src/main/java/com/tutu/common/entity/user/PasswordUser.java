package com.tutu.common.entity.user;

/**
 * Minimal contract for password-based login verification.
 *
 * <p>Both global users and tenant-scoped users can implement this interface.</p>
 */
public interface PasswordUser {
    String getPassword();

    void setPassword(String password);

    String getStatus();
}

