package util;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class ClickEffectUtil {

    public static void applyButtonEffects(Node parent) {
        for (Node node : parent.lookupAll(".button")) {
            node.setOnMousePressed(e -> animateScale(node, 0.96, 0.96, 100));
            node.setOnMouseReleased(e -> animateScale(node, 1.0, 1.0, 100));
        }
    }

    public static void applyCardHoverEffects(Node parent) {
        for (Node node : parent.lookupAll(".hover-card")) {
            node.setOnMouseEntered(e -> animateScale(node, 1.02, 1.02, 150));
            node.setOnMouseExited(e -> animateScale(node, 1.0, 1.0, 150));
        }
    }

    private static void animateScale(Node node, double x, double y, int millis) {
        ScaleTransition st = new ScaleTransition(Duration.millis(millis), node);
        st.setToX(x);
        st.setToY(y);
        st.play();
    }
}