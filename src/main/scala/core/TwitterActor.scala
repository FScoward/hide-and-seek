package core

import akka.actor.Actor
import frame.Tweet
import twitter4j.TwitterFactory
import collection.JavaConversions._
import scala.concurrent.duration.DurationInt

/**
 * Created by FScoward on 2014/11/30.
 */

class TwitterActor extends Actor {
  private[this] val twitter = TwitterFactory.getSingleton
  val interval = 5 minute

  def getScrollText = {
    twitter.getHomeTimeline.map(status => {
      new Tweet(s"""${status.getText.replaceAll("\r", "")} @${status.getUser.getScreenName}""", 1920, 20)
    }).toList
  }
  
  def receive = {
    case x: Int => {
      println("--- RECEIVE ---")
      if(!TweetStack.stack.isEmpty) {
        Thread.sleep(interval.toMillis)
        TweetStack.stack.clear
      }

      // timeline 取得
      sender ! getScrollText
    }
    case _ =>
  }
}
