package nl.uva.larissa.json.model;


// TODO original testStatementWithScore1 had raw 679.0 and min 0.0, although spec says decimal number
// also SCORM 2004 4th edition says something about 7 digits and claims raw is superseded but defines it anyway!
public class Score {
	Float scaled;
	Integer raw;
	Integer min;
	Integer max;

	public Float getScaled() {
		return scaled;
	}

	public void setScaled(Float scaled) {
		this.scaled = scaled;
	}

	public Integer getRaw() {
		return raw;
	}

	public void setRaw(Integer raw) {
		this.raw = raw;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

}
