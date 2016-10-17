import org.specs2.mutable.Specification

/**
  * Created by Samy on 13/10/2016.
  */
object LogicSpec extends Specification {
  "The 'matchLikelihood' method" should {
    "be 100% when all attributes match" in {
      val tabby = Kitten(1, List("male", "tabby"))
      val prefs = Preference(List("male", "tabby"))
      val result = Logic.matchLikelihood(tabby, prefs)
      result must beGreaterThan(.999)
    }
  }

  "The 'matchLikelihood' method" should {
    "be 0% when no attributes match" in {
      val tabby = Kitten(1, List("male", "tabby"))
      val prefs = Preference(List("female", "calico"))
      val result = Logic.matchLikelihood(tabby, prefs)
      result must beLessThan(.001)
    }
  }

  "The 'matchLikelihood' method" should {
    "be 50% when the half of attributes match" in {
      val tabby = Kitten(1, List("male","tabby"))
      val prefs = Preference(List("male", "notabby"))
      val result = Logic.matchLikelihood(tabby, prefs)
      result must beEqualTo(0.5)
    }
  }
}
