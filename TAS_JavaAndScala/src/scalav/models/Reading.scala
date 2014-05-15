package scalav.models

class Reading(ind : Int, pe1 : Double, pd1 : Double, pt1 : Double, 
		pe2 : Double, pd2 : Double, pt2 : Double, pe3 : Double, pd3 : Double, pt3 : Double) {
	var pe = Array.ofDim[Double](3)
	pe(0) = pe1 
	pe(1) = pe2
	pe(2) = pe3
	var pd = Array.ofDim[Double](3)
	pd(0) = pd1 
	pd(1) = pd2
	pd(2) = pd3
	var pt = Array.ofDim[Double](3)
	pt(0) = pt1 
	pt(1) = pt2
	pt(2) = pt3
}