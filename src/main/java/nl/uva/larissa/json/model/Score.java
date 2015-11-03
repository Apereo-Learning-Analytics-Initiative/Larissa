package nl.uva.larissa.json.model;


// TODO original testStatementWithScore1 had raw 679.0 and min 0.0, although spec says decimal number
// also SCORM 2004 4th edition says something about 7 digits and claims raw is superseded but defines it anyway!
public class Score {
	Float scaled;
	Float raw;
	Float min;
	Float max;

	public Float getScaled() {
		return scaled;
	}

	public void setScaled(Float scaled) {
		this.scaled = scaled;
	}

	public Float getRaw() {
		return raw;
	}

	public void setRaw(Float raw) {
		this.raw = raw;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}

}
