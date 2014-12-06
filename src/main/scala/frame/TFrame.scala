package frame

import java.awt.Graphics
import javax.swing.{JPanel, UIManager, JFrame}

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import env.Environment
import scala.concurrent.duration.DurationInt
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global
import core.{TweetStack, TwitterActor}

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
  
  Environment.maxX = rectangle.getMaxX.toInt
  Environment.maxY = rectangle.getMaxY.toInt
  
  setBounds(0, (Environment.maxY - 20), Environment.maxX, 20)
  setUndecorated(true)
  setOpacity(0.4f)
//    setBackground(new Color(0, 0, 0, 0))
  setAlwaysOnTop(true)

  setContentPane(new TPanel(rectangle.getMaxX.toInt))

  setVisible(true)
}

class TPanel(width: Int) extends JPanel with Runnable {
  // ダブルバッファリング
  setDoubleBuffered(true)

  var initPos = width
  val lb = new ListBuffer[Tweet]
  val actor = ActorSystem("actor").actorOf(Props[TwitterActor])
  implicit val timeout = Timeout(10 minutes)
  var tweet: Option[Tweet] = None
  
  def run = {
    while(true) {
      /*
      (actor ? 1).onComplete {
        case Success(s) => stack.push(new Tweet("test", 1920, 20))
        case Failure(_) => println("failure")
      }
      */
      
      (actor ? 1).onSuccess {
        case list: List[Tweet] => {
          list.map(TweetStack.stack.push(_))
        }
        case _ => println("None")
      }
      
//      stack.foreach{x => if(x.move() < 0) stack.pop()}
      if(tweet.isEmpty && !TweetStack.stack.isEmpty) {
        tweet = Option(TweetStack.stack.pop())
      } else {
        tweet.foreach(t => if(t.move() < 0 - t.length * 24) tweet = None)
      }
      
      
      revalidate()
      repaint()
//      initPos += 50 + text.length
      Thread.sleep(8)
    }
  }
  
  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
//    stack.foreach(_.draw(g))
    tweet.foreach(_.draw(g))
  }

  new Thread(this).start()
}
