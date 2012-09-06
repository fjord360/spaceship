package spacepack;
import java.util.ArrayList;

public class Buildmap {
	
	public ArrayList<Star> star = new ArrayList<Star>();
	
	// 별 추가
	public void addStar(String name) {
		star.add(new Star(name));
	}
	
	// 별 데이터에 정보 입력
	public void addInfo(int num, String field, String dbval) {		
		if( field.equals("이름") ) star.get(num).setName(dbval);
		if( field.equals("타입") ) star.get(num).setType(dbval);
		if( field.equals("형태") ) star.get(num).setForm(dbval);
		if( field.equals("적경_시") ) star.get(num).setAsc(0, dbval);
		if( field.equals("적경_분") ) star.get(num).setAsc(1, dbval);
		if( field.equals("적경_초") ) star.get(num).setAsc(2, dbval);
		if( field.equals("적위_도") ) star.get(num).setDec(0, dbval);
		if( field.equals("적위_분") ) star.get(num).setDec(1, dbval);
		if( field.equals("적위_초") ) star.get(num).setDec(2, dbval);
		if( field.equals("거리") ) star.get(num).setDistance(dbval);
		if( field.equals("밝기") ) star.get(num).setBrightness(dbval);
		if( field.equals("크기") ) star.get(num).setSize(dbval);
		if( field.equals("질량") ) star.get(num).setMass(dbval);
		if( field.equals("온도") ) star.get(num).setKelvin(dbval);
		if( field.equals("어미항성") ) star.get(num).setMother(dbval);
		if( field.equals("궤도거리") ) star.get(num).setSemimajor(dbval);
		if( field.equals("공전주기") ) star.get(num).setPeriod(dbval);
		if( field.equals("텍스쳐") ) star.get(num).setTexture(dbval);
		if( field.equals("고리") ) star.get(num).setDisk(dbval);
	}
	
	// 렌더링 형태로 변환
	public String ConvertType(int num) {
		String answer = "";
		if( star.get(num).getType().equals("star") ) {
			answer += BuildStar(star.get(num).getForm());
		}
		else if( star.get(num).getType().equals("planet") ) {
			if( star.get(num).getMotherStr().equals("태양") ) {
				answer += BuildPlanet(star.get(num).getMassEJtoMJ(), star.get(num).getKelvin());
			} else {
				answer += BuildPlanet(star.get(num).getMass(), star.get(num).getKelvin());
			}
		}
		else {
			answer += "암석행성@J";
		}
		return answer;
	}
	
	// 별 정보 리턴
	public String getStarInfo(int first, int num) {
		String info = "";
		String conv = ConvertType(num);
		String[] split = conv.split("@");
		info += "@" + split[1];
		
		info += "@" + (star.get(num).getAscCoord() - star.get(0).getAscCoord());
		info += "@" + (star.get(num).getDecCoord() - star.get(0).getDecCoord());
		info += "@" + (star.get(num).getDistance() - star.get(0).getDistance());
		info += "@" + star.get(num).getSemimajorCalc();
		info += "@" + star.get(num).getPeriod();
		info += "@" + star.get(num).getInclination();
		info += "@" + star.get(num).getSizeCalc();
		info += "@" + star.get(num).getName() + "!" + star.get(num).getTexture() + "!" + star.get(num).getDisk() + "!" + star.get(num).getMotherStr();
		
		// 기준별만 있는 정보
		if( first == 0 && num == 0 ) {
			if( star.get(num).getMotherStr().equals("태양") ) {
				info += "@" + "형태 : " + split[0];
				info += "@" + "거리 : " + star.get(num).getDistanceStr() + " AU";
				info += "@" + "질량 : " + star.get(num).getMassStr() + " 지구질량";
				info += "@" + "크기 : " + star.get(num).getSizeREtoKMStr() + " km";
				info += "@" + "온도 : " + star.get(num).getKelvinStr() + " K";
				info += "@" + "궤도거리 : " + star.get(num).getSemimajorStr() + " AU";
				info += "@" + "공전주기 : " + star.get(num).getPeriodConvertStr();
			} else {
				if( star.get(num).getType().equals("satellite") ) {
					info += "@" + "형태 : " + split[0];
					info += "@" + "거리 : " + star.get(num).getDistanceStr() + " AU";
					info += "@" + "질량 : " + star.get(num).getMassStr() + " x 10^19 kg";
					info += "@" + "크기 : " + star.get(num).getSizeStr() + " km";
					info += "@" + "온도 : " + star.get(num).getKelvinStr() + " K";
					info += "@" + "궤도거리 : " + star.get(num).getSemimajorStr() + " km";
					info += "@" + "공전주기 : " + star.get(num).getPeriodStr() + " 일";
				}
				else {
					info += "@" + "형태 : " + split[0];
					info += "@" + "적경 : " + star.get(num).getAscStr();
					info += "@" + "적위 : " + star.get(num).getDecStr();
					info += "@" + "거리 : " + star.get(num).getDistanceStr() + " 광년";
					info += "@" + "밝기 : " + star.get(num).getBrightnessStr() + " 태양광도";
					info += "@" + "질량 : " + star.get(num).getMassStr() + " 목성질량";
					info += "@" + "크기 : " + star.get(num).getSizeStr() + " 태양반경";
				}
			}
		}
		return info;
	}
	
	// DB에 저장된 행성의 형태를 보고 렌더링 형태로 변환 
	public String BuildPlanet(double mass, double kelvin) {
		String class_type = "";
		
		// 질량이 0.04 MJ 이상이면 가스행성으로 분류
		if( mass >= 0.04f ) {
			class_type = "가스행성";
			if( kelvin >= 1400.0f ) class_type += "@A";
			else if( kelvin >= 800.0f && kelvin < 1400.0f ) class_type += "@B";
			else if( kelvin >= 350.0f && kelvin < 800.0f ) class_type += "@C";
			else if( kelvin >= 150.0f && kelvin < 350.0f ) class_type += "@D";
			else class_type += "@E";
		} else {
			class_type = "암석행성";
			if( mass >= 0.04f ) class_type += "@F";
			else if( mass >= 0.032f && mass < 0.04f ) class_type += "@G";
			else if( mass >= 0.024f && mass < 0.032f ) class_type += "@H";
			else if( mass >= 0.018f && mass < 0.024f ) class_type += "@I";
			else class_type += "@J";
		}
		
		return class_type;
	}

	// DB에 저장된 별의 형태를 보고 렌더링 형태로 변환
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
	
		return class_type + "@" + class_spec + class_grade + class_aff;
	}
}
