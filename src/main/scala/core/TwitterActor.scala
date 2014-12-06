package core

import akka.actor.Actor
import env.Environment
import frame.Tweet
import twitter4j.{Paging, TwitterFactory}
import collection.JavaConversions._
import scala.concurrent.duration.DurationInt

/**
 * Created by FScoward on 2014/11/30.
 */

class TwitterActor extends Actor {
  private[this] val twitter = TwitterFactory.getSingleton
  val interval = 1 minute

  private [this] def getScrollText = {
    println("get scroll text")
    twitter.getHomeTimeline(new Paging(1, 100)).map(status => {
      new Tweet( s"""【${status.getUser.getName}@${status.getUser.getScreenName}】 ${status.getText.replaceAll("\r", "")}""", Environment.maxX, 20)
    }).toList.reverse
  }

  override def receive = {
    case x: Int => {
      println("--- RECEIVE ---")
      if (!TweetStack.stack.isEmpty) {
        Thread.sleep(interval.toMillis)
        TweetStack.stack.clear
      }
      getScrollText.foreach(t => TweetStack.stack.push(t))
    }
    case _ =>
  }
}
