package spacepack;

public class Buildmap {
	
	public String BuildPlanet(double mass, double kelvin) {
		String class_type = "";
		
		// ������ 0.04 MJ �̻��̸� �����༺���� �з�
		if( mass >= 0.04f ) {
			class_type = "�����༺";
			if( kelvin >= 1400.0f ) class_type += "!A";
			else if( kelvin >= 800.0f && kelvin < 1400.0f ) class_type += "!B";
			else if( kelvin >= 350.0f && kelvin < 800.0f ) class_type += "!C";
			else if( kelvin >= 150.0f && kelvin < 350.0f ) class_type += "!D";
			else class_type += "!E";
		} else {
			class_type = "�ϼ��༺";
			if( mass >= 0.04f ) class_type += "!F";
			else if( mass >= 0.032f && mass < 0.04f ) class_type += "!G";
			else if( mass >= 0.024f && mass < 0.032f ) class_type += "!H";
			else if( mass >= 0.018f && mass < 0.024f ) class_type += "!I";
			else class_type += "!J";
		}
		
		return class_type;
	}

	public String BuildStar(String type) {
		String class_type = "�����ּ�";
		String class_spec = "M";
		String class_grade = "0";
		String class_aff = "B";
		boolean _nonetype = false;
		if( type.length() == 0 ) type = "M0B";
		
		// �б��� �м�
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
		
		// ���� �б��� �м�
		if( !_nonetype && type.length() > 1 ) {
			// ������ ��츸
			if( type.charAt(1) >= 48 || type.charAt(1) <= 58 ) {
				class_grade = Character.toString(type.charAt(1));
			}
			if( class_grade.equals("") || class_grade.equals(" ") ) class_grade = "0";
		}
		
		// �迭 �м�
		// V�� - �ְ迭��
		if( type.indexOf("V") > 0 ) {
			// IV�� - �ż�
			if( type.indexOf("IV") > 0 ) {
				class_aff = "E";
				class_type = "�ذż�";
			}
			// V�� - �ְ迭��
			else {
				class_aff = "D";
				class_type = "�ְ迭��";
			}
		}
		else if( type.indexOf("I") > 0 ) {
			if( type.indexOf("II") > 0 ) {
				// III�� - �ż�
				if( type.indexOf("III") > 0 ) {
					class_aff = "F";
					class_type = "�ż�";
				}
				// // II�� - �����ż�
				else {
					class_aff = "G";
					class_type = "�����ż�";
				}
			}
			else {
				// I�� - �ش�ż�
				if( type.indexOf("Ia0") > 0 || type.indexOf("Ia+") > 0 || type.indexOf("Ia-0") > 0 ) {
					class_aff = "I";
					class_type = "�ش�ż�";
				}
				// I�� - �ʰż�
				else {
					class_aff = "H";
					class_type = "�ʰż�";
				}
			}
		}
		// D�� : ����ּ�
		if( class_spec.equals("D") ) {
			class_aff = "C";
			class_type = "����ּ�";
		}
		// W�� : �ش�ż�
		else if( class_spec.equals("W") ) {
			class_aff = "I";
			class_type = "�ش�ż�";
		}
		// LTY�� : �����ּ�
		else if( class_spec.equals("L") || class_spec.equals("T") || class_spec.equals("Y") ) {
			class_aff = "A";
			class_type = "�����ּ�";
		}
	
		return class_type + "!" + class_spec + class_grade + class_aff;
	}
	
	// ��Ʈ�����¿��� ��հ��� ��������
	public double ParseValueAverage(String text) {
		double value = 0.0f;
		text.replaceAll(",", "");
		if( text.indexOf("~") > 0 ) {
			String[] split = text.split("~");
			double value1 = Double.parseDouble(split[0]);
			double value2 = Double.parseDouble(split[1]);
			value = (value1 + value2) / 2.0f;
		}
		else if( text.indexOf("��") > 0 ) {
			String[] split = text.split("��");
			value = Double.parseDouble(split[0]);
		}
		
		return value;
	}
}
