package htwg.minesweeperse.util

import htwg.minesweeperse.util.{Observable, Observer}
import org.scalatest.wordspec.AnyWordSpec

class ObserversWSTest extends AnyWordSpec {

  "Observable" should {

    "add and notify observers" in {
      var updated = false

      // Dummy-Observer, der update() setzt
      val observer = new Observer {
        override def update(): Unit = updated = true
      }

      val observable = new Observable {}
      observable.addObserver(observer)
      observable.notifyObservers()

      assert(updated) // Prüft ob geupdated wurde, also true ist
    }

    "remove observers" in {
      var updateCount = 0

      val observer = new Observer {
        override def update(): Unit = updateCount += 1
      }

      val observable = new Observable {}
      // Füge hinzu, entferne wieder
      observable.addObserver(observer)
      observable.removeObserver(observer)
      observable.notifyObservers()

      assert(updateCount == 0) // Prüft ob entfernt wurde
    }
  }
}