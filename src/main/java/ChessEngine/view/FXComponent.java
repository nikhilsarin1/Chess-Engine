package ChessEngine.view;

import javafx.scene.Parent;

/**
 * The FXComponent interface represents a JavaFX component that can be rendered as a Parent node.
 * Implementing classes must provide an implementation for the 'render' method.
 */
public interface FXComponent {

  /**
   * Renders the JavaFX component and returns its root Parent node.
   *
   * @return the root Parent node of the rendered component
   */
  Parent render();
}
