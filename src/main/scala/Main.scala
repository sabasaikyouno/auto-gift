import scalafx.application.JFXApp3
import scalafx.scene.Scene

object Main extends JFXApp3 {
  override def start() = {
    stage = new JFXApp3.PrimaryStage {
      title.value = "auto-gift"
      width = 600
      height = 450
      scene = new Scene {

      }
    }
  }
}
