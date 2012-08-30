package spacepack;

public class Buildmap {
	
	public String BuildPlanet(double mass, double kelvin) {
		String class_type = "";
		
		// 질량이 0.04 MJ 이상이면 가스행성으로 분류
		if( mass >= 0.04f ) {
			class_type = "가스행성";
			if( kelvin >= 1400.0f ) class_type += "!A";
			else if( kelvin >= 800.0f && kelvin < 1400.0f ) class_type += "!B";
			else if( kelvin >= 350.0f && kelvin < 800.0f ) class_type += "!C";
			else if( kelvin >= 150.0f && kelvin < 350.0f ) class_type += "!D";
			else class_type += "!E";
		} else {
			class_type = "암석행성";
			if( mass >= 0.04f ) class_type += "!F";
			else if( mass >= 0.032f && mass < 0.04f ) class_type += "!G";
			else if( mass >= 0.024f && mass < 0.032f ) class_type += "!H";
			else if( mass >= 0.018f && mass < 0.024f ) class_type += "!I";
			else class_type += "!J";
		}
		
		return class_type;
	}

	public String BuildStar(String type) {
		String class_type = "적색왜성";
		String class_spec = "M";
		String class_grade = "0";
		String class_aff = "B";
		boolean _nonetype = false;
		if( type.length() == 0 ) type = "M0B";
		
		// 분광형 분석
		if( type.charAt(0) == 'D' ) class_spec = "D";
		else if( type.charAt(0) == 'W' ) class_spec = "W";
		else if( type.charAt(0) == 'O' ) class_spec = "O";
		else if( type.charAt(0) == 'B' ) class_spec = "B";
		else if( type.charAt(0) == 'A' ) class_spec = "A";
		else if( type.charAt(0) == 'F' ) class_spec = "F";
		else if( type.charAt(0) == 'G' ) class_spec = "G";
		else if( type.charAt(0) == 'K' ) class_spec = "K";
		else if( type.charAt(0) == 'L' ) class_spec = "L";
		else if( type.charAt(0) == 'T' ) class_spec = "T";
		else if( type.charAt(0) == 'Y' ) class_spec = "Y";
		else {
			_nonetype = true;
		}
		
		// 세부 분광형 분석
		if( !_nonetype && type.length() > 1 ) {
			// 숫자일 경우만
			if( type.charAt(1) >= 48 || type.charAt(1) <= 58 ) {
				class_grade = Character.toString(type.charAt(1));
			}
			if( class_grade.equals("") || class_grade.equals(" ") ) class_grade = "0";
		}
		
		// 계열 분석
		// V형 - 주계열성
		if( type.indexOf("V") > 0 ) {
			// IV형 - 거성
			if( type.indexOf("IV") > 0 ) {
				class_aff = "E";
				class_type = "준거성";
			}
			// V형 - 주계열성
			else {
				class_aff = "D";
				class_type = "주계열성";
			}
		}
		else if( type.indexOf("I") > 0 ) {
			if( type.indexOf("II") > 0 ) {
				// III형 - 거성
				if( type.indexOf("III") > 0 ) {
					class_aff = "F";
					class_type = "거성";
				}
				// // II형 - 밝은거성
				else {
					class_aff = "G";
					class_type = "밝은거성";
				}
			}
			else {
				// I형 - 극대거성
				if( type.indexOf("Ia0") > 0 || type.indexOf("Ia+") > 0 || type.indexOf("Ia-0") > 0 ) {
					class_aff = "I";
					class_type = "극대거성";
				}
				// I형 - 초거성
				else {
					class_aff = "H";
					class_type = "초거성";
				}
			}
		}
		// D형 : 백색왜성
		if( class_spec.equals("D") ) {
			class_aff = "C";
			class_type = "백색왜성";
		}
		// W형 : 극대거성
		else if( class_spec.equals("W") ) {
			class_aff = "I";
			class_type = "극대거성";
		}
		// LTY형 : 갈색왜성
		else if( class_spec.equals("L") || class_spec.equals("T") || class_spec.equals("Y") ) {
			class_aff = "A";
			class_type = "갈색왜성";
		}
	
		return class_type + "!" + class_spec + class_grade + class_aff;
	}
	
	// 스트링형태에서 평균값만 리턴해줌
	public double ParseValueAverage(String text) {
		double value = 0.0f;
		text.replaceAll(",", "");
		if( text.indexOf("~") > 0 ) {
			String[] split = text.split("~");
			double value1 = Double.parseDouble(split[0]);
			double value2 = Double.parseDouble(split[1]);
			value = (value1 + value2) / 2.0f;
		}
		else if( text.indexOf("±") > 0 ) {
			String[] split = text.split("±");
			value = Double.parseDouble(split[0]);
		}
		
		return value;
	}
}
