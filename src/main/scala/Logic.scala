object Logic {

  def mathLikelihood(k: Kitten, p: Preference): Double ={
    val nb = p.attributes.map(att => k.attributes.contains(att)).map(x => if(x) 1.0 else 0)
    nb.sum/nb.length
  }
}