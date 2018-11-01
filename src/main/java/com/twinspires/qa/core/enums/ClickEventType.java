package com.twinspires.qa.core.enums;

/**
 * Created by kasey.sparkman on 6/01/2018.
 *
 * ClickEventTypeIds for legibility and ease of use
 */
public enum ClickEventType {
    NULL(0, "NULL", "NULL"),
    MENU_TODAYS_RACES(1,	"Todays Races", "Main Navigation Menus"),
    MENU_RESULTS_REPLAYS(2, "Results / Replays", "Main Navigation Menus"),
    MENU_CHANGES(3, "Changes", "Main Navigation Menus"),
    MENU_SUPPORT(4, "Support", "Main Navigation Menus"),
    MENU_DEPOSIT(5, "Deposit", "Main Navigation Menus"),
    MENU_SIGN_IN(6, "Sign In", "Main Navigation Menus"),
    MENU_FORGOT_PASSWORD(7, "Forgot Password", "Main Navigation Menus"),
    MENU_USERNAME(8, "Username", "Main Navigation Menus"),
    MENU_ACCT_HISTORY(9, "Account History", "Main Navigation Menus"),
    MENU_CASHIER(10, "Cashier", "Main Navigation Menus"),
    MENU_LOGIN_SETTINGS(11, "Login Settings", "Main Navigation Menus"),
    MENU_SIGN_OUT(12, "Sign Out", "Main Navigation Menus"),
    BS_TABS_BET_SLIP(13, "Bet Slip", "Bet Slip Tabs"),
    BS_TABS_MY_BETS(14, "My Bets", "Bet Slip Tabs"),
    BS_TABS_COMPLETED(15, "Completed", "Bet Slip Tabs"),
    VIDEO_STREAMING(16, "Streaming", "Video Tabs"),
    VIDEO_REPLAYS(17, "Replays", "Video Tabs"),
    MTB_BAR_TRACK(18, "Track", "MTP Bar"),
    MTB_BAR_TOTE_BOARD(19, "Tote Board", "MTP Bar"),
    HIDE_SHOW_POOLS(20, "Hide / Show", "Pools Section"),
    HIDE_SHOW_PROBABLES(21, "Hide / Show", "Probables Section"),
    HIDE_SHOW_WILL_PAYS(22, "Hide / Show", "Will Pays Section"),
    PROGRAM_RUNNER_DETAILS(23, "Runner Details", "Program"),
    MENU_JOIN(24, "Join Now", "Main Navigation Menus"),
    REG_NEXT_STEP(25, "Next Step", "Registration"),
    REG_TERMS(26, "Terms", "Registration"),
    REG_BACK(27, "Back", "Registration"),
    REG_CREATE_ACCOUNT(28, "Create Account", "Registration"),
    PROGRAM_TRAINER(29, "Trainer Details", "Program"),
    RUN_DETAILS_TRAINER(30, "Trainer Details", "Runner Details"),
    PROGRAM_JOCKEY(31, "Jockey Details", "Program"),
    RUN_DETAILS_JOCKEY(32, "Jockey Details", "Runner Details"),
    MENU_OFFERS(33, "Offers", "Main Navigation Menus"),
    REG_SSN_TOOLTIP(34, "SSN Tool Tip", "Registration"),
    VIDEO_TRAY(35, "Video Tray Icon", "Video"),
    VIDEO_TRAY_RETURN(36, "Return To Tray Icon", "Video"),
    VIDEO_POP_OUT(37, "Pop Out Icon", "Video"),
    MENU_BET(38, "Bet Menu", "Main Navigation Menus"),
    TOUR_PREV_IMAGE(39, "Previous Image", "Tour Image Carousel"),
    TOUR_NEXT_IMAGE(40, "Next Image", "Tour Image Carousel"),
    TOUR_CLOSE(41, "Close", "Tour Image Carousel"),
    MENU_FREE_BETS(42, "Free Bets", "Main Navigation Menus"),
    FREE_BETS_EMAIL(43, "Email", "Free Bets"),
    FREE_BETS_COPY(44, "Copy", "Free Bets"),
    FREE_BETS_FACEBOOK(45, "Facebook", "Free Bets"),
    FREE_BETS_TWITTER(46, "Twitter", "Free Bets"),
    PROGRAM_DAM_DETAILS(47, "Dam Details", "Program"),
    RUN_DETAILS_DAM_DETAILS(48, "Dam Details", "Runner Details"),
    PROGRAM_SIRE_DETAILS(49, "Sire Details", "Program"),
    RUN_DETAILS_SIRE_DETAILS(50, "Sire Details", "Runner Details"),
    SUPPORT_CHAT_REQUEST(51, "Chat Request", "Support"),
    MENU_BETS(52, "Bets", "Main Navigation"),
    MY_BETS_VIDEO(53, "Video", "My Bets"),
    MY_BETS_COPY(54, "Copy", "My Bets"),
    VIDEO_TRACK_SELECTION(55, "Track Selection", "Video"),
    VIDEO_RACE_SELECTION(56, "Race Selection", "Video"),
    TOOL_TIP_JOCKEY(57, "Jockey Tool Tip", "Tool Tip"),
    TOOL_TIP_TRAINER(58, "Trainer Tool Tip", "Tool Tip"),
    PROGRAM_BASIC(59, "Basic", "Program"),
    PROGRAM_SPEED(60, "Speed", "Program"),
    PROGRAM_CLASS(61, "Class", "Program"),
    PROGRAM_PACE(62, "Pace", "Program"),
    MENU_CALENDAR(63, "Calendar", "Main Navigation Menus"),
    CALENDAR_BET(64, "Bet Now", "Calendar"),
    CALENDAR_CARRYOVER(65, "Carryover", "Calendar"),
    ACCT_UPDATE_EMAIL(66, "Email update ", "My Account"),
    ACCT_UPDATE_PASSWORD(67, "PW update", "My Account"),
    ACCT_UPDATE_PIN(68, "Pin update", "My Account"),
    ACCT_UPDATE_PHONE(69, "Phone update", "My Account"),
    MENU_TOUR(70, "Start Tour", "Main Navigation Menus"),
    FAVORITE_ADDED(71, "Added Favorite Track", "All"),
    FAVORITE_REMOVED(72, "Removed Favorite Track", "All"),
    MENU_MY_ACCOUNT(73, "My Account", "Main Navigation Menus"),
    ACCT_UPDATE_ADDRESS_TOOL_TIP(74, "Address tool tip", "My Account"),
    ACCT_UPDATE_BACK_BUTTON(75, "Back Button", "My Account"),
    CC_DEPOSIT_BET_BUTTON(77, "Credit Card Deposit And Bet Button", "Bet Share"),
    BET_SLIP_BET_SHARE_BUTTON(137, "Bet Share Button", "Bet Share"),
    BET_SLIP_BET_SHARE_TOOLTIP_CLOSE(138, "Bet Share Tooltip Close", "Bet Share"),
    BET_SHARE_TERMS_CLOSE_BUTTON(139, "Bet Share Terms Close Button", "Bet Share"),
    BET_SHARE_TERMS_GOTIT_BUTTON(140, "Bet Share Terms Got It Button", "Bet Share"),
    BET_SLIP_BET_SHARE_SPLIT_INCREASE(141, "Bet Share Split Increase", "Bet Share"),
    BET_SLIP_BET_SHARE_SPLIT_DECREASE(142, "Bet Share Split Decrease", "Bet Share"),
    BET_SLIP_BET_SHARE_RESERVE_INCREASE(143, "Bet Share Reserve Increase", "Bet Share"),
    BET_SLIP_BET_SHARE_RESERVE_DECREASE(144, "Bet Share Reserve Decrease", "Bet Share"),
    BET_SLIP_BET_SHARE_SUCCESS_LATER_BUTTON(145, "Bet Share Success Later Button", "Bet Share"),
    BET_SLIP_BET_SHARE_SUCCESS_SHARE_BUTTON(146, "Bet Share Success Share Button", "Bet Share"),
    TOOLTIP_BET_SHARE_SPLIT(147, "Tooltip Bet Share Split", "Bet Share"),
    TOOLTIP_BET_SHARE_RESERVE(148, "Tooltip Bet Share Reserve", "Bet Share"),
    TOOLTIP_BET_SHARE_BET_TOTAL(149, "Tooltip Bet Share Bet Total", "Bet Share"),
    BET_SLIP_EMAIL_SHARE_BET(166, "Bet Slip Email Share Bet", "Bet Share"),
    BET_SLIP_TEXT_SHARE_BET(167, "Bet Slip Text Share Bet", "Bet Share"),
    BET_SLIP_COPY_SHARE_BET(168, "Bet Slip Copy Share Bet", "Bet Share"),
    BET_SLIP_FACEBOOK_SHARE_BET(169, "Bet Slip Facebook Share Bet", "Bet Share"),
    BET_SLIP_TWITTER_SHARE_BET(170, "Bet Slip Twitter Share Bet", "Bet Share"),
    BET_SLIP_BET_SHARE_CLOSED_DELETE_BUTTON(212, "Bet Share Closed Delete Button", "Bet Share"),
    BET_SLIP_BET_SHARE_CLOSED_SUBMIT_BUTTON(213, "Bet Share Closed Delete Button", "Bet Share"),
    BET_SHARE_JOIN_INCREASE_SHARES(214, "Bet Share Join Increase Shares Button", "Bet Share"),
    BET_SHARE_JOIN_DECREASE_SHARES(215, "Bet Share Join Decrease Shares Button", "Bet Share"),
    BET_SHARE_JOIN_NO_THANKS_BUTTON(216, "Bet Share Join No Thanks Button", "Bet Share"),
    BET_SHARE_JOIN_JOIN_BET_BUTTON(217, "Bet Share Join join Bet Button", "Bet Share"),
    BET_SHARE_JOIN_HOW_IT_WORKS_LINK(218, "Bet Share Join How It Works Link", "Bet Share"),
    BET_SHARE_JOIN_CLOSED_OKAY_BUTTON(219, "Bet Share Join Closed Bet Okay Button", "Bet Share"),
    BET_SHARE_JOIN_RESTRICTED_OKAY_BUTTON(220, "Bet Share Join Restricted State Okay Button", "Bet Share"),
    BET_SHARE_JOIN_ALL_CLAIMED_OKAY_BUTTON(221, "Bet Share Join All Claimed Okay Button", "Bet Share"),
    BET_SHARE_JOIN_INVALID_ID_OKAY_BUTTON(222, "Bet Share Join Invalid ID Okay Button", "Bet Share"),
    LOGIN_PAGE_X(224, "Login Page X Button", "Login Page"),
    BET_SHARE_JOIN_REGISTRATION_PAGE_X(225, "Bet Share Join Registration Page X", "Bet Share"),
    BET_SHARE_JOIN_DEPOSIT_X(226, "Bet Share Join Deposit Page X", "Bet Share"),
    LOGIN_PAGE_SIGNIN_BUTTON(227, "Sign In Button", "Login Page"),
    LOGIN_JOIN_NOW_BUTTON(228, "Join Now Button", "Login Page"),
    BET_SHARE_JOIN_DEPOSIT_BACK_ARROW(229, "Bet Share Join Deposit Back Button", "Bet Share"),
    BET_SHARE_JOIN_DEPOSIT_EDIT_BUTTON(230, "Bet Share Join Deposit Edit Button", "Bet Share"),
    EZ_MONEY_DEPOSIT_BET_BUTTON(231, "EZ Money Deposit And Bet Button", "Bet Share");

    private Integer id;
    private String type;
    private String section;

    ClickEventType(Integer typeId, String typeName, String typeSection) {
        this.id = typeId;
        this.type = typeName;
        this.section = typeSection;
    }

    public int getId() {
        return this.id.intValue();
    }

    public String getName() {
        return this.type;
    }

    public String getSection() {
        return this.section;
    }

    public static ClickEventType getById(String findId) {
        return getById(Integer.valueOf(findId));
    }
    public static ClickEventType getById(Integer findId) {
        for(ClickEventType e : values()) {
            if(e.id == findId) {
                return e;
            }
        }
        return null;
    }
}