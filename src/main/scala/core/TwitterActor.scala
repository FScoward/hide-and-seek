package core

import akka.actor.Actor
import twitter4j.TwitterFactory
import collection.JavaConversions._

/**
 * Created by FScoward on 2014/11/30.
 */

class TwitterActor extends Actor {
  private[this] val twitter = TwitterFactory.getSingleton
  
  def getScrollText = {
    twitter.getHomeTimeline.map(status => {
      s"""${status.getText} @${status.getUser.getScreenName}"""
    }).toList
  }
  
  def receive = {
    case x: Int => {
      Thread.sleep(10000)
      
      // timeline 取得
      
      sender ! "Success"
    }
    case _ =>
  }
}
