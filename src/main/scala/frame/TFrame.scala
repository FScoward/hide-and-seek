package frame

import java.awt.{Color, Graphics}
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
  
  setBounds(0, (Environment.maxY - 20), Environment.maxX, 30)
  setUndecorated(true)
  setOpacity(0.4f)
//  setBackground(new Color(0, 0, 0, 0))
  setAlwaysOnTop(true)

  setContentPane(new TPanel(rectangle.getMaxX.toInt))

  setVisible(true)
}

class TPanel(width: Int) extends JPanel with Runnable {
  // ダブルバッファリング
  setDoubleBuffered(true)
  
  setBackground(Color.BLACK)

  var initPos = width
  val lb = new ListBuffer[Tweet]
  val actor = ActorSystem("actor").actorOf(Props[TwitterActor])
  implicit val timeout = Timeout(20 minutes)
  var tweet: Option[Tweet] = None
  val tweets = new mutable.Stack[Tweet]
  /*
   * TODO
   * 前のツイートが画面半分まで流れたら次を流し始める
   */
  // 次を描画しても良いか否か
  var canNext: Boolean = false
  
  def run = {
    while(true) {

      actor ! 1
      
      if(tweet.isEmpty && !TweetStack.stack.isEmpty) {
        println("pop ... rest is " + TweetStack.stack.length)
        tweet = Option(TweetStack.stack.pop())
      } else {
        tweet.foreach(t => if(t.move() < 0 - t.length * Environment.fontSize) tweet = None)
      }
      
      revalidate()
      repaint()
       
      Thread.sleep(15)
    }
  }
  
  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    tweet.foreach(_.draw(g))
  }

  new Thread(this).start()
}
