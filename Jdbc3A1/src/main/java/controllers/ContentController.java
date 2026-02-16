package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import models.Content;
import services.ContentService;
import utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContentController {

    // top bar (search + user placeholder)
    @FXML private TextField globalSearch;
    @FXML private Button userBtn;

    // Optional: some layouts show a small title in the top bar
    @FXML private Label pageTitle;

    // nav
    @FXML private Button homeBtn;
    @FXML private Button articlesBtn;
    @FXML private Button videosBtn;
    @FXML private Button podcastsBtn;
    @FXML private Button adminBtn;

    // root areas
    @FXML private StackPane centerStack;
    @FXML private VBox homePane;
    @FXML private VBox forumPane;

    // home sections
    @FXML private HBox topBlogsRow;
    @FXML private VBox recommendedList;

    // forum filters
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> sortFilter;
    @FXML private VBox forumList;

    private final ContentService contentService = new ContentService();
    private List<Content> current = new ArrayList<>();
    private String currentType = "All";

    @FXML
    public void initialize() {
        // Don't hard-fail if optional nodes are missing (keeps layouts flexible).

        // filters
        typeFilter.setItems(FXCollections.observableArrayList("All", "Article", "Video", "Podcast"));
        typeFilter.setValue("All");

        rebuildCategoryFilter();

        sortFilter.setItems(FXCollections.observableArrayList("Latest", "Top (score)", "Most Upvoted"));
        sortFilter.setValue("Latest");

        searchField.textProperty().addListener((o, a, b) -> applyForumFilters());
        typeFilter.valueProperty().addListener((o, a, b) -> applyForumFilters());
        categoryFilter.valueProperty().addListener((o, a, b) -> applyForumFilters());
        sortFilter.valueProperty().addListener((o, a, b) -> applyForumFilters());

        // default page
        handleHome();
    }

    private void rebuildCategoryFilter() {
        List<String> cats = new ArrayList<>();
        cats.add("All");
        cats.addAll(contentService.getDistinctCategories());
        categoryFilter.setItems(FXCollections.observableArrayList(cats));
        categoryFilter.setValue("All");
    }

    // ---------- Navigation ----------

    @FXML public void handleHome() {
        currentType = "All";
        if (pageTitle != null) pageTitle.setText("Home");

        show(homePane);

        loadHomeSections();
    }

    @FXML public void handleArticles() { openForum("Article"); }

    @FXML public void handleVideos() { openForum("Video"); }

    @FXML public void handlePodcasts() { openForum("Podcast"); }

    // Top bar actions
    @FXML
    public void onGlobalSearch() {
        if (globalSearch == null) return;
        String q = safe(globalSearch.getText()).trim();
        if (q.isBlank()) return;

        openForum("All");
        if (searchField != null) searchField.setText(q);
        applyForumFilters();
    }

    @FXML
    public void onUserClick() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Not implemented");
        a.setHeaderText(null);
        a.setContentText("User profile / login will be integrated later.");
        a.showAndWait();
    }

    private void openForum(String type) {
        currentType = type;
        if (pageTitle != null) pageTitle.setText(type + "s");

        show(forumPane);

        rebuildCategoryFilter();
        typeFilter.setValue(type); // sets currentType via applyForumFilters
        loadForumData();
        applyForumFilters();
    }

    private void show(Region pane) {
        centerStack.getChildren().forEach(n -> n.setVisible(false));
        pane.setVisible(true);
        pane.toFront();
    }

    // ---------- Data ----------

    private void loadHomeSections() {
        List<Content> top = contentService.getTopThisWeek(6);
        List<Content> latest = contentService.getLatestContent(10);

        // top blogs row
        topBlogsRow.getChildren().clear();
        for (Content c : top) topBlogsRow.getChildren().add(createTopCard(c));

        // recommended = latest content
        recommendedList.getChildren().clear();
        for (Content c : latest) recommendedList.getChildren().add(createFeedRow(c));

        // categories might change after additions
        rebuildCategoryFilter();
    }

    private void loadForumData() {
        if ("All".equalsIgnoreCase(currentType)) {
            current = contentService.getAllContent();
        } else {
            // Use all and filter by normalized type so messy data still shows
            current = contentService.getAllContent().stream()
                    .filter(c -> normalizeType(c.getType()).equalsIgnoreCase(currentType))
                    .collect(Collectors.toList());
        }
    }

    private void applyForumFilters() {
        // ensure data exists
        if (!forumPane.isVisible()) return;
        if (current == null) loadForumData();

        String q = safe(searchField.getText()).toLowerCase().trim();
        String type = safe(typeFilter.getValue(), "All");
        String cat = safe(categoryFilter.getValue(), "All");
        String sort = safe(sortFilter.getValue(), "Latest");

        List<Content> filtered = current.stream()
                .filter(c -> q.isEmpty()
                        || safe(c.getTitle()).toLowerCase().contains(q)
                        || safe(c.getDescription()).toLowerCase().contains(q)
                        || safe(c.getCategory()).toLowerCase().contains(q))
                .filter(c -> "All".equalsIgnoreCase(type) || normalizeType(c.getType()).equalsIgnoreCase(type))
                .filter(c -> "All".equalsIgnoreCase(cat) || safe(c.getCategory()).equalsIgnoreCase(cat))
                .collect(Collectors.toList());

        filtered.sort((a, b) -> {
            return switch (sort) {
                case "Most Upvoted" -> Integer.compare(b.getUpvotes(), a.getUpvotes());
                case "Top (score)" -> Integer.compare(b.getScore(), a.getScore());
                default -> {
                    // Latest
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) yield Integer.compare(b.getId(), a.getId());
                    if (a.getCreatedAt() == null) yield 1;
                    if (b.getCreatedAt() == null) yield -1;
                    yield b.getCreatedAt().compareTo(a.getCreatedAt());
                }
            };
        });

        forumList.getChildren().clear();
        for (Content c : filtered) forumList.getChildren().add(createFeedRow(c));
    }

    // ---------- UI Builders ----------

    private Region createTopCard(Content c) {
        VBox card = new VBox(10);
        card.getStyleClass().add("top-card");
        card.setPrefWidth(320);
        card.setPadding(new Insets(12));

        // Thumb area with overlays (type badge + category pill)
        StackPane thumb = new StackPane();
        thumb.getStyleClass().add("card-thumb");
        thumb.setPrefSize(296, 160);

        ImageView img = new ImageView();
        img.setFitWidth(296);
        img.setFitHeight(160);
        img.setPreserveRatio(false);

        Image loaded = ImageUtil.loadSmartImage(c.getImageUrl(), 296, 160);
        img.setImage(loaded);

        // Center placeholder icon if no image
        Label centerIcon = new Label(typeIcon(c.getType()));
        centerIcon.setStyle("-fx-font-size: 44px; -fx-text-fill: rgba(17,24,39,0.25);");
        centerIcon.setVisible(loaded == null);

        Label typeBadge = new Label(typeIcon(c.getType()));
        typeBadge.getStyleClass().add("type-badge");
        StackPane.setAlignment(typeBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(typeBadge, new Insets(10, 10, 0, 0));

        Label catPill = new Label(safe(c.getCategory(), "General"));
        catPill.getStyleClass().add("category-pill");
        StackPane.setAlignment(catPill, Pos.BOTTOM_LEFT);
        StackPane.setMargin(catPill, new Insets(0, 0, 10, 10));

        thumb.getChildren().addAll(img, centerIcon, typeBadge, catPill);

        Label title = new Label(safe(c.getTitle(), "Untitled"));
        title.getStyleClass().add("top-card-title");
        title.setWrapText(true);

        Label meta = new Label(buildMeta(c));
        meta.getStyleClass().add("muted");

        card.getChildren().addAll(thumb, title, meta);
        card.setOnMouseClicked(e -> openPostDetail(c));
        return card;
    }

    private Region createFeedRow(Content c) {
        HBox row = new HBox(12);
        row.getStyleClass().add("feed-row");
        row.setAlignment(Pos.TOP_LEFT);
        row.setPadding(new Insets(10));

        ImageView thumb = new ImageView();
        thumb.setFitWidth(90);
        thumb.setFitHeight(60);
        thumb.setPreserveRatio(true);
        thumb.getStyleClass().add("feed-thumb");
        thumb.setImage(ImageUtil.loadSmartImage(c.getImageUrl(), 90, 60));

        VBox middle = new VBox(4);
        middle.setAlignment(Pos.TOP_LEFT);

        Label title = new Label(safe(c.getTitle(), "Untitled"));
        title.getStyleClass().add("feed-title");
        title.setWrapText(true);

        Label meta = new Label(buildMeta(c));
        meta.getStyleClass().add("muted");

        Label desc = new Label(trim(safe(c.getDescription()), 160));
        desc.getStyleClass().add("feed-desc");
        desc.setWrapText(true);

        middle.getChildren().addAll(title, meta, desc);

        VBox votes = createVoteBox(c);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(thumb, middle, spacer, votes);
        row.setOnMouseClicked(e -> openPostDetail(c));

        // prevent vote buttons from triggering open
        votes.setOnMouseClicked(e -> e.consume());

        return row;
    }

    private VBox createVoteBox(Content c) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.TOP_CENTER);

        Button up = new Button("▲");
        up.getStyleClass().add("vote-btn");
        Button down = new Button("▼");
        down.getStyleClass().add("vote-btn");

        Label score = new Label(String.valueOf(c.getScore()));
        score.getStyleClass().add("vote-score");

        up.setOnAction(e -> {
            contentService.upvoteContent(c.getId());
            c.setUpvotes(c.getUpvotes() + 1);
            score.setText(String.valueOf(c.getScore()));
        });
        down.setOnAction(e -> {
            contentService.downvoteContent(c.getId());
            c.setDownvotes(c.getDownvotes() + 1);
            score.setText(String.valueOf(c.getScore()));
        });

        box.getChildren().addAll(up, score, down);
        return box;
    }

    // ---------- Navigation to detail ----------

    private void openPostDetail(Content c) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/PostDetail.fxml")));
            Parent root = loader.load();

            PostDetailController controller = loader.getController();
            controller.setContent(c);

            // swap into current scene
            centerStack.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Navigation error");
            a.setHeaderText(null);
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
    }

    // ---------- Helpers ----------

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

    private String typeIcon(String type) {
        String t = normalizeType(type);
        if ("Video".equalsIgnoreCase(t)) return "🎥";
        if ("Podcast".equalsIgnoreCase(t)) return "🎧";
        return "📄"; // Article / default
    }

    private String safe(String s) { return s == null ? "" : s; }

    private String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }

    private String trim(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 1)) + "…";
    }
}
