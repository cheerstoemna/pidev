package test;

import models.CoachingPlan;
import services.CoachingPlanService;

public class Main {
    public static void main(String[] args) {

        CoachingPlanService cps = new CoachingPlanService();

        CoachingPlan plan = new CoachingPlan(
                1,
                "Stress Management",
                "Reduce anxiety",
                "Practice breathing daily",
                null
        );

        cps.addCoachingPlan(plan);
    }
}
