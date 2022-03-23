package me.alexpresso.zuninja.classes.challenge;

import me.alexpresso.zuninja.classes.projection.action.ActionType;

import java.util.Optional;

public enum ChallengeType {
    FUSE(ActionType.FUSION),
    INVOKE(ActionType.INVOCATION),
    ASCEND(ActionType.ASCENSION),
    TRADE,
    UPGRADE(ActionType.ENCHANT),
    CRAFT(ActionType.CRAFT),
    META_CHALLENGE,
    LOOT,
    RECYCLE(ActionType.RECYCLE),
    INVOKE_ITEMS,
    TRADE_USERS,
    LUCKY(ActionType.LUCKY_RAYOU),
    WEEKLY(ActionType.WEEKLY),
    UNKNOWN;


    private final ActionType actionType;

    ChallengeType(){
        this(null);
    }
    ChallengeType(final ActionType actionType) {
        this.actionType = actionType;
    }

    public Optional<ActionType> getActionType() {
        return Optional.ofNullable(this.actionType);
    }
}
