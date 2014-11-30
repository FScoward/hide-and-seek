package frame

import java.awt.Graphics
import javax.swing.{JPanel, UIManager, JFrame}

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import scala.concurrent.duration.DurationInt
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global
import core.TwitterActor

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}


/**
 * Created by FScoward on 2014/11/20.
 */
class TFrame extends JFrame {
  private[this] final val rectangle = getGraphicsConfiguration.getBounds

  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  setTitle("Hide and Seek")
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setBounds(0, (rectangle.getMaxY - 20).toInt, (rectangle.getMaxX).toInt, 20)
  setUndecorated(true)
  setOpacity(0.4f)
//    setBackground(new Color(0, 0, 0, 0))

  setContentPane(new TPanel(rectangle.getMaxX.toInt))

  setVisible(true)
}

class TPanel(width: Int) extends JPanel with Runnable {
  // ダブルバッファリング
  setDoubleBuffered(true)

  var initPos = width
  val lb = new ListBuffer[Tweet]
  val stack = new mutable.Stack[Tweet]
  val actor = ActorSystem("actor").actorOf(Props[TwitterActor])
  implicit val timeout = Timeout(1000 seconds)
  
  def run = {
    while(true) {
      val text = "test"
      
      (actor ? 1).onComplete {
        case Success(s) => stack.push(new Tweet("test", 1920, 20))
        case Failure(_) => println("failure")
      }
      
      println(stack.length)
      stack.foreach{x => if(x.move() < 0) stack.pop()}
      revalidate()
      repaint()
      initPos += 50 + text.length * 20
      Thread.sleep(10)
    }
  }
  
  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    stack.foreach(_.draw(g))
  }

  new Thread(this).start()
}
