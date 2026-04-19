package org.example.rawabet.enums;

public enum SubscribeResultType {
    /** The subscription starts today and is immediately active. */
    ACTIVATED_NOW,
    /** The subscription is queued and will start after the current active subscription ends. */
    QUEUED_NEXT
}
