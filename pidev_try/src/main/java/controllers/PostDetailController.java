package controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.AppUser;
import models.Comment;
import models.Content;
import services.AnalyticsService;
import services.CommentService;
import services.ContentService;
import utils.ImageUtil;
import utils.UserSession;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

public class PostDetailController {

    @FXML private Label titleLabel;
    @FXML private Label metaLabel;
    @FXML private ImageView heroImage;
    @FXML private Label descLabel;

    @FXML private Button backBtn;
    @FXML private Button openLinkBtn;

    @FXML private Button upBtn;
    @FXML private Button downBtn;
    @FXML private Label scoreLabel;

    @FXML private TextArea commentInput;
    @FXML private Button addCommentBtn;

    @FXML private VBox commentsBox;

    private final ContentService contentService = new ContentService();
    private final CommentService commentService = new CommentService();

    private Content content;
    private Runnable onBack;
    private final AnalyticsService analytics = new AnalyticsService();
    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    @FXML
    private void goBack() {
        if (onBack != null) onBack.run();
    }

    public void setContent(Content c) {
        this.content = c;
        if (c == null) return;

        // log view (only if logged in, no alerts)
        AppUser u = UserSession.get().getUser();
        if (u != null) {
            analytics.logEvent(u.getId(), c.getId(), "VIEW", 1);
        }

        renderContent();
        loadComments();
    }

    private void renderContent() {
        if (content == null) return;

        titleLabel.setText(safe(content.getTitle(), "Untitled"));
        metaLabel.setText(buildMeta(content));
        descLabel.setText(safe(content.getDescription()));

        heroImage.setImage(ImageUtil.loadSmartImage(content.getImageUrl(), 900, 260));

        scoreLabel.setText(String.valueOf(content.getScore()));

        openLinkBtn.setDisable(safe(content.getSourceUrl()).isBlank());
    }

    private Integer requireLoggedUserId() {
        AppUser u = UserSession.get().getUser();
        if (u == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Not logged in");
            a.setHeaderText(null);
            a.setContentText("Please login to vote/comment.");
            a.showAndWait();
            return null;
        }
        return u.getId(); // your AppUser constructor uses (id, name, role) -> id getter should exist
    }

    @FXML
    public void openSourceLink() {
        if (content == null) return;
        String url = safe(content.getSourceUrl()).trim();
        if (url.isBlank()) return;

        try {
            Desktop.getDesktop().browse(new URI(url));
            AppUser u = UserSession.get().getUser();
            if (u != null && content != null) {
                analytics.logEvent(u.getId(), content.getId(), "OPEN_LINK", 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    // -------------------- CONTENT VOTES (TOGGLE) --------------------

    @FXML
    public void upvotePost() {
        if (content == null) return;

        Integer uid = requireLoggedUserId();
        if (uid == null) return;

        // ✅ toggle upvote
        contentService.upvoteContent(content.getId(), uid);
        analytics.logEvent(uid, content.getId(), "UPVOTE", 4);
        // refresh from DB (reliable)
        refreshContentFromDb();
    }

    @FXML
    public void downvotePost() {
        if (content == null) return;

        Integer uid = requireLoggedUserId();
        if (uid == null) return;

        // ✅ toggle downvote
        contentService.downvoteContent(content.getId(), uid);
        analytics.logEvent(uid, content.getId(), "DOWNVOTE", -2);
        refreshContentFromDb();
    }

    private void refreshContentFromDb() {
        try {
            Content fresh = contentService.getById(content.getId());
            if (fresh != null) {
                this.content = fresh;
                renderContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- COMMENTS --------------------

    @FXML
    public void addComment() {
        if (content == null) return;

        Integer uid = requireLoggedUserId();
        if (uid == null) return;

        String text = commentInput.getText() == null ? "" : commentInput.getText().trim();

        if (text.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Write a comment first.", ButtonType.OK).showAndWait();
            return;
        }

        if (text.length() > 2000) {
            new Alert(Alert.AlertType.WARNING, "Please keep comments under 2000 characters.", ButtonType.OK).showAndWait();
            return;
        }

        commentService.addComment(content.getId(), uid, text);
        analytics.logEvent(uid, content.getId(), "COMMENT", 3);
        commentInput.clear();
        loadComments();
    }

    private void loadComments() {
        if (content == null || commentsBox == null) return;

        List<Comment> list = commentService.getCommentsByContentId(content.getId());
        commentsBox.getChildren().clear();

        for (Comment c : list) {
            commentsBox.getChildren().add(buildCommentCard(c));
        }
    }

    private VBox buildCommentCard(Comment c) {
        VBox card = new VBox(8);
        card.getStyleClass().add("comment-card");
        card.setPadding(new Insets(12));

        // header
        String uname = safe(c.getUserName());
        if (uname.isBlank()) uname = "User #" + c.getUserId();

        Label user = new Label(uname);
        user.getStyleClass().add("comment-user");

        Label date = new Label(c.getCreatedAt() == null ? "" : c.getCreatedAt().toString());
        date.getStyleClass().add("muted");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, user, spacer, date);
        header.setAlignment(Pos.CENTER_LEFT);

        // body
        Label body = new Label(safe(c.getText()));
        body.setWrapText(true);
        body.getStyleClass().add("comment-text");

        // votes (TOGGLE)
        Button up = new Button("▲");
        up.getStyleClass().add("vote-btn");

        Button down = new Button("▼");
        down.getStyleClass().add("vote-btn");

        Label score = new Label(String.valueOf(c.getScore()));
        score.getStyleClass().add("vote-score");

        up.setOnAction(e -> {
            Integer uid = requireLoggedUserId();
            if (uid == null) return;

            commentService.upvoteComment(c.getId(), uid);
            loadComments(); // simplest accurate refresh
        });

        down.setOnAction(e -> {
            Integer uid = requireLoggedUserId();
            if (uid == null) return;

            commentService.downvoteComment(c.getId(), uid);
            loadComments();
        });

        HBox votes = new HBox(8, up, score, down);
        votes.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(header, body, votes);
        return card;
    }

    // -------------------- helpers --------------------

    private String buildMeta(Content c) {
        String t = normalizeType(c.getType());
        if (t.isBlank()) t = "Article";
        String cat = safe(c.getCategory(), "General");
        return t + " • " + cat;
    }

    private String normalizeType(String raw) {
        String t = safe(raw).trim().toLowerCase();
        if (t.isEmpty()) return "";
        if (t.contains("article") || t.contains("text") || t.equals("blog") || t.equals("post")) return "Article";
        if (t.contains("video")) return "Video";
        if (t.contains("podcast") || t.contains("audio")) return "Podcast";
        return raw.trim();
    }

    private String safe(String s) { return s == null ? "" : s; }
    private String safe(String s, String fallback) { return (s == null || s.isBlank()) ? fallback : s; }
}