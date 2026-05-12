package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import models.CoachingPlan;
import models.Role;
import services.AdviceService;
import services.CoachingPlanService;
import services.ExerciseProgressService;
import services.ExerciseService;
import services.FavoritePlanService;
import services.QuoteService;
import services.TranslateService;
import utils.AppState;
import utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private AnchorPane contentArea;
    @FXML private VBox contentRoot;

    @FXML private FlowPane favoritesContainer;
    @FXML private FlowPane myPlansContainer;
    @FXML private FlowPane otherPlansContainer;
    @FXML private VBox favoritesSection;
    @FXML private VBox myPlansSection;
    @FXML private VBox otherPlansSection;

    @FXML private Label quoteTitleLabel;
    @FXML private Label adviceTitleLabel;
    @FXML private Label quoteLabel;
    @FXML private Label adviceLabel;

    @FXML private ComboBox<String> langCombo;
    @FXML private Button applyLangBtn;

    @FXML private Label languageLabel;
    @FXML private Label favoritesTitle;
    @FXML private Label myPlansTitle;
    @FXML private Label otherPlansTitle;
    @FXML private Button addPlanBtn;

    private final CoachingPlanService planService = new CoachingPlanService();
    private final QuoteService quoteService = new QuoteService();
    private final AdviceService adviceService = new AdviceService();
    private final ExerciseService exerciseService = new ExerciseService();
    private final ExerciseProgressService progressService = new ExerciseProgressService();
    private final TranslateService translateService = new TranslateService();
    private final FavoritePlanService favoriteService = new FavoritePlanService();

    private final int loggedInUserId = UserSession.get().userId();
    private final Role loggedInUserRole = UserSession.get().getUser().getRole();

    private String lastQuoteOriginal = null;
    private String lastAdviceOriginal = null;

    @FXML
    public void initialize() {
        if (favoritesContainer == null || myPlansContainer == null || otherPlansContainer == null
                || contentArea == null || quoteLabel == null) {
            throw new IllegalStateException("FXML injection failed for dashboard.");
        }

        if (langCombo != null) {
            langCombo.getItems().setAll("English (en)", "Français (fr)");
            String cur = AppState.getCoachingLang();
            langCombo.setValue("fr".equals(cur) ? "Français (fr)" : "English (en)");
        }

        applyStaticUiLanguage();
        applyRoleVisibility();
        loadPlans();
        loadQuoteAsync();
        loadAdviceAsync();
    }

    public void refreshQuote() {
        loadQuoteAsync();
    }

    @FXML
    private void onApplyLanguage() {
        if (langCombo == null || langCombo.getValue() == null) return;

        String selected = langCombo.getValue();
        String lang = selected.contains("(fr)") ? "fr" : "en";
        AppState.setCoachingLang(lang);

        applyStaticUiLanguage();
        applyRoleVisibility();
        loadPlans();
        translateExistingQuoteAsync();
        translateExistingAdviceAsync();
    }

    private void applyStaticUiLanguage() {
        String lang = AppState.getCoachingLang();
        if (lang == null) lang = "en";

        boolean coachView = loggedInUserRole == Role.COACH;

        if ("fr".equals(lang)) {
            if (languageLabel != null) languageLabel.setText("Langue :");
            if (applyLangBtn != null) applyLangBtn.setText("Appliquer");
            if (addPlanBtn != null) addPlanBtn.setText("+ Ajouter un plan");

            if (favoritesTitle != null) {
                favoritesTitle.setText(coachView ? "Plan le plus favori" : "Plans favoris");
            }
            if (myPlansTitle != null) myPlansTitle.setText("Mes plans");
            if (otherPlansTitle != null) otherPlansTitle.setText("Explorer d'autres plans");

            if (quoteTitleLabel != null) quoteTitleLabel.setText("Une phrase de sagesse");
            if (adviceTitleLabel != null) adviceTitleLabel.setText("Essayez ce conseil");
        } else {
            if (languageLabel != null) languageLabel.setText("Language:");
            if (applyLangBtn != null) applyLangBtn.setText("Apply");
            if (addPlanBtn != null) addPlanBtn.setText("+ Add Plan");

            if (favoritesTitle != null) {
                favoritesTitle.setText(coachView ? "Top Favorite Plan" : "Favorite Plans");
            }
            if (myPlansTitle != null) myPlansTitle.setText("My Coaching Plans");
            if (otherPlansTitle != null) otherPlansTitle.setText("Explore Other Plans");

            if (quoteTitleLabel != null) quoteTitleLabel.setText("One-line wisdom");
            if (adviceTitleLabel != null) adviceTitleLabel.setText("Try this tip");
        }
    }

    private void applyRoleVisibility() {
        boolean coachView = loggedInUserRole == Role.COACH;

        setNodeVisible(addPlanBtn, coachView);
        setNodeVisible(myPlansSection, coachView);
    }

    private void loadPlans() {
        favoritesContainer.getChildren().clear();
        myPlansContainer.getChildren().clear();
        otherPlansContainer.getChildren().clear();

        List<CoachingPlan> plans = planService.getAllPlans();
        Map<Integer, Integer> favoriteCounts = favoriteService.getFavoriteCountsByPlan();
        Set<Integer> userFavoriteIds = favoriteService.getFavoritePlanIds(loggedInUserId);

        boolean coachView = loggedInUserRole == Role.COACH;

        if (coachView) {
            List<CoachingPlan> coachPlans = plans.stream()
                    .filter(plan -> plan.getUserId() == loggedInUserId)
                    .collect(Collectors.toList());

            Optional<CoachingPlan> topFavoritePlan = coachPlans.stream()
                    .filter(plan -> favoriteCounts.getOrDefault(plan.getPlanId(), 0) > 0)
                    .sorted(Comparator
                            .comparingInt((CoachingPlan plan) -> favoriteCounts.getOrDefault(plan.getPlanId(), 0))
                            .reversed()
                            .thenComparing(CoachingPlan::getPlanId, Comparator.reverseOrder()))
                    .findFirst();

            if (topFavoritePlan.isPresent()) {
                CoachingPlan plan = topFavoritePlan.get();
                favoritesContainer.getChildren().add(
                        createPlanCard(plan, false, false, favoriteCounts.getOrDefault(plan.getPlanId(), 0), true)
                );
            } else {
                favoritesContainer.getChildren().add(createEmptyFavoriteState());
            }

            for (CoachingPlan plan : plans) {
                if (plan.getUserId() == loggedInUserId) {
                    myPlansContainer.getChildren().add(
                            createPlanCard(plan, false, false, favoriteCounts.getOrDefault(plan.getPlanId(), 0), false)
                    );
                } else {
                    otherPlansContainer.getChildren().add(
                            createPlanCard(plan, false, false, favoriteCounts.getOrDefault(plan.getPlanId(), 0), false)
                    );
                }
            }
        } else {
            List<CoachingPlan> favoritePlans = plans.stream()
                    .filter(plan -> userFavoriteIds.contains(plan.getPlanId()))
                    .collect(Collectors.toList());

            for (CoachingPlan plan : favoritePlans) {
                favoritesContainer.getChildren().add(
                        createPlanCard(plan, true, true, favoriteCounts.getOrDefault(plan.getPlanId(), 0), false)
                    );
            }

            for (CoachingPlan plan : plans) {
                if (plan.getUserId() != loggedInUserId) {
                    otherPlansContainer.getChildren().add(
                        createPlanCard(plan, true, userFavoriteIds.contains(plan.getPlanId()), favoriteCounts.getOrDefault(plan.getPlanId(), 0), false)
                    );
                }
            }
        }
    }

    private void loadQuoteAsync() {
        if (!quoteLabel.getStyleClass().contains("quote-body")) {
            quoteLabel.getStyleClass().add("quote-body");
        }
        quoteLabel.setText("Loading quote...");

        Thread t = new Thread(() -> {
            try {
                String quote = quoteService.fetchQuoteText();
                lastQuoteOriginal = quote;

                String display = quote;
                String lang = AppState.getCoachingLang();
                if (lang != null && !"en".equals(lang)) {
                    try {
                        display = translateService.translate(quote, lang);
                    } catch (Exception te) {
                        te.printStackTrace();
                        display = quote;
                    }
                }

                String finalDisplay = display;
                Platform.runLater(() -> quoteLabel.setText(finalDisplay));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> quoteLabel.setText("Could not load quote."));
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void translateExistingQuoteAsync() {
        if (lastQuoteOriginal == null || lastQuoteOriginal.isBlank()) {
            loadQuoteAsync();
            return;
        }

        Thread t = new Thread(() -> {
            try {
                String display = lastQuoteOriginal;
                String lang = AppState.getCoachingLang();

                if (lang != null && !"en".equals(lang)) {
                    display = translateService.translate(lastQuoteOriginal, lang);
                }

                String finalDisplay = display;
                Platform.runLater(() -> quoteLabel.setText(finalDisplay));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> quoteLabel.setText(lastQuoteOriginal));
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void loadAdviceAsync() {
        if (adviceLabel == null) return;

        if (!adviceLabel.getStyleClass().contains("advice-body")) {
            adviceLabel.getStyleClass().add("advice-body");
        }
        adviceLabel.setText("Loading advice...");

        Thread t = new Thread(() -> {
            try {
                String advice = adviceService.fetchAdvice();
                lastAdviceOriginal = advice;

                String display = advice;
                String lang = AppState.getCoachingLang();
                if (lang != null && !"en".equals(lang)) {
                    try {
                        display = translateService.translate(advice, lang);
                    } catch (Exception ignored) {
                        display = advice;
                    }
                }

                String finalDisplay = display;
                Platform.runLater(() -> adviceLabel.setText(finalDisplay));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> adviceLabel.setText("Could not load advice."));
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void translateExistingAdviceAsync() {
        if (adviceLabel == null) return;

        if (lastAdviceOriginal == null || lastAdviceOriginal.isBlank()) {
            loadAdviceAsync();
            return;
        }

        String lang = AppState.getCoachingLang();
        if (lang == null || "en".equals(lang)) {
            adviceLabel.setText(lastAdviceOriginal);
            return;
        }

        Thread t = new Thread(() -> {
            try {
                String tr = translateService.translate(lastAdviceOriginal, lang);
                Platform.runLater(() -> adviceLabel.setText(tr));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> adviceLabel.setText(lastAdviceOriginal));
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private AnchorPane createPlanCard(CoachingPlan plan, boolean allowFavoriteToggle, boolean initiallyFavorite,
                                      int favoriteCount, boolean readOnlyFavorite) {
        AnchorPane card = new AnchorPane();
        card.setPrefSize(210, 220);
        card.setMinSize(210, 220);
        card.setMaxSize(210, 220);
        card.getStyleClass().add("plan-card");
        card.setStyle("-fx-cursor: hand;");

        ImageView planImage = new ImageView();
        planImage.setFitWidth(160);
        planImage.setFitHeight(110);
        planImage.setPreserveRatio(true);
        planImage.setSmooth(true);
        planImage.setLayoutX(25);
        planImage.setLayoutY(15);

        Image img = loadPlanImage(plan.getImagePath());
        if (img != null) planImage.setImage(img);

        Label favoriteBadge = new Label((initiallyFavorite ? "♥ " : "♡ ") + favoriteCount);
        favoriteBadge.setLayoutX(150);
        favoriteBadge.setLayoutY(8);
        favoriteBadge.setPrefWidth(46);
        favoriteBadge.setAlignment(javafx.geometry.Pos.CENTER);
        favoriteBadge.getStyleClass().add("plan-favorite-badge");
        favoriteBadge.getStyleClass().add(initiallyFavorite ? "plan-favorite-active" : "plan-favorite-idle");

        if (allowFavoriteToggle && !readOnlyFavorite) {
            favoriteBadge.getStyleClass().add("plan-favorite-clickable");
            favoriteBadge.setOnMouseClicked(e -> {
                e.consume();
                int planId = plan.getPlanId();

                if (favoriteService.isFavorite(loggedInUserId, planId)) {
                    favoriteService.remove(loggedInUserId, planId);
                } else {
                    favoriteService.add(loggedInUserId, planId);
                }
                loadPlans();
            });
        }

        Label planTitle = new Label();
        planTitle.setLayoutX(16);
        planTitle.setLayoutY(132);
        planTitle.setPrefWidth(178);
        planTitle.setWrapText(true);
        planTitle.setTextAlignment(TextAlignment.CENTER);
        planTitle.getStyleClass().add("plan-card-title");
        planTitle.setAlignment(javafx.geometry.Pos.CENTER);

        String rawTitle = plan.getTitle() == null ? "" : plan.getTitle();
        planTitle.setText(rawTitle);

        String lang = AppState.getCoachingLang();
        if (lang != null && !"en".equals(lang) && !rawTitle.isBlank()) {
            Thread t = new Thread(() -> {
                try {
                    String tr = translateService.translate(rawTitle, lang);
                    Platform.runLater(() -> planTitle.setText(tr));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            t.setDaemon(true);
            t.start();
        }

        int planId = plan.getPlanId();
        int total = exerciseService.getExercisesByPlan(planId).size();
        int completed = progressService.countCompletedForPlan(planId, loggedInUserId);

        ProgressBar pb = new ProgressBar(total == 0 ? 0.0 : (double) completed / (double) total);
        pb.setPrefWidth(170);
        pb.setPrefHeight(8);
        pb.setLayoutX(20);
        pb.setLayoutY(196);

        Label progressText = new Label(completed + "/" + total);
        progressText.setLayoutX(20);
        progressText.setLayoutY(180);
        progressText.setPrefWidth(170);
        progressText.setAlignment(javafx.geometry.Pos.CENTER);
        progressText.getStyleClass().add("plan-progress-text");

        card.getChildren().addAll(planImage, favoriteBadge, planTitle, progressText, pb);
        card.setPickOnBounds(true);
        card.setOnMouseClicked(e -> openPlanDetails(plan));

        return card;
    }

    private Label createEmptyFavoriteState() {
        Label empty = new Label("No plans liked yet.");
        empty.setWrapText(true);
        empty.setPrefWidth(240);
        empty.setMinHeight(120);
        empty.getStyleClass().add("plan-empty-state");
        return empty;
    }

    private Image loadPlanImage(String imagePath) {
        if (imagePath != null && !imagePath.isBlank()) {
            try {
                File f = new File(imagePath);
                if (f.exists() && f.isFile()) {
                    return new Image(f.toURI().toString(), true);
                }
            } catch (Exception ignored) {
            }
        }

        try (InputStream is = getClass().getResourceAsStream("/fxml/images/plan.png")) {
            if (is != null) return new Image(is);
        } catch (Exception ignored) {
        }

        return null;
    }

    private void openPlanDetails(CoachingPlan plan) {
        try {
            List<Node> snapshot = new ArrayList<>(contentArea.getChildren());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlanDetails.fxml"));
            Parent view = loader.load();

            PlanDetailsController controller = loader.getController();
            controller.setContext(contentArea);
            controller.setPreviousContent(snapshot);
            controller.setOnReturnRefresh(this::loadPlans);
            controller.setOwner(plan.getUserId() == loggedInUserId && loggedInUserRole == Role.COACH);
            controller.setLoggedInUserId(loggedInUserId);
            controller.setPlan(plan);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddPlan() {
        if (loggedInUserRole != Role.COACH) return;

        try {
            List<Node> snapshot = new ArrayList<>(contentArea.getChildren());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddPlan.fxml"));
            Parent view = loader.load();

            AddPlanController controller = loader.getController();
            controller.setContext(contentArea);
            controller.setPreviousContent(snapshot);
            controller.setOnReturnRefresh(this::loadPlans);
            controller.setLoggedInUserId(loggedInUserId);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setNodeVisible(Node node, boolean visible) {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
