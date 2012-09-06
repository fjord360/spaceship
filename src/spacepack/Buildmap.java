package spacepack;
import java.util.ArrayList;

public class Buildmap {
	
	public ArrayList<Star> star = new ArrayList<Star>();
	
	// �� �߰�
	public void addStar(String name) {
		star.add(new Star(name));
	}
	
	// �� �����Ϳ� ���� �Է�
	public void addInfo(int num, String field, String dbval) {		
		if( field.equals("�̸�") ) star.get(num).setName(dbval);
		if( field.equals("Ÿ��") ) star.get(num).setType(dbval);
		if( field.equals("����") ) star.get(num).setForm(dbval);
		if( field.equals("����_��") ) star.get(num).setAsc(0, dbval);
		if( field.equals("����_��") ) star.get(num).setAsc(1, dbval);
		if( field.equals("����_��") ) star.get(num).setAsc(2, dbval);
		if( field.equals("����_��") ) star.get(num).setDec(0, dbval);
		if( field.equals("����_��") ) star.get(num).setDec(1, dbval);
		if( field.equals("����_��") ) star.get(num).setDec(2, dbval);
		if( field.equals("�Ÿ�") ) star.get(num).setDistance(dbval);
		if( field.equals("���") ) star.get(num).setBrightness(dbval);
		if( field.equals("ũ��") ) star.get(num).setSize(dbval);
		if( field.equals("����") ) star.get(num).setMass(dbval);
		if( field.equals("�µ�") ) star.get(num).setKelvin(dbval);
		if( field.equals("����׼�") ) star.get(num).setMother(dbval);
		if( field.equals("�˵��Ÿ�") ) star.get(num).setSemimajor(dbval);
		if( field.equals("�����ֱ�") ) star.get(num).setPeriod(dbval);
		if( field.equals("�ؽ���") ) star.get(num).setTexture(dbval);
		if( field.equals("��") ) star.get(num).setDisk(dbval);
	}
	
	// ������ ���·� ��ȯ
	public String ConvertType(int num) {
		String answer = "";
		if( star.get(num).getType().equals("star") ) {
			answer += BuildStar(star.get(num).getForm());
		}
		else if( star.get(num).getType().equals("planet") ) {
			if( star.get(num).getMotherStr().equals("�¾�") ) {
				answer += BuildPlanet(star.get(num).getMassEJtoMJ(), star.get(num).getKelvin());
			} else {
				answer += BuildPlanet(star.get(num).getMass(), star.get(num).getKelvin());
			}
		}
		else {
			answer += "�ϼ��༺@J";
		}
		return answer;
	}
	
	// �� ���� ����
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
		
		// ���غ��� �ִ� ����
		if( first == 0 && num == 0 ) {
			if( star.get(num).getMotherStr().equals("�¾�") ) {
				info += "@" + "���� : " + split[0];
				info += "@" + "�Ÿ� : " + star.get(num).getDistanceStr() + " AU";
				info += "@" + "���� : " + star.get(num).getMassStr() + " ��������";
				info += "@" + "ũ�� : " + star.get(num).getSizeREtoKMStr() + " km";
				info += "@" + "�µ� : " + star.get(num).getKelvinStr() + " K";
				info += "@" + "�˵��Ÿ� : " + star.get(num).getSemimajorStr() + " AU";
				info += "@" + "�����ֱ� : " + star.get(num).getPeriodConvertStr();
			} else {
				if( star.get(num).getType().equals("satellite") ) {
					info += "@" + "���� : " + split[0];
					info += "@" + "�Ÿ� : " + star.get(num).getDistanceStr() + " AU";
					info += "@" + "���� : " + star.get(num).getMassStr() + " x 10^19 kg";
					info += "@" + "ũ�� : " + star.get(num).getSizeStr() + " km";
					info += "@" + "�µ� : " + star.get(num).getKelvinStr() + " K";
					info += "@" + "�˵��Ÿ� : " + star.get(num).getSemimajorStr() + " km";
					info += "@" + "�����ֱ� : " + star.get(num).getPeriodStr() + " ��";
				}
				else {
					info += "@" + "���� : " + split[0];
					info += "@" + "���� : " + star.get(num).getAscStr();
					info += "@" + "���� : " + star.get(num).getDecStr();
					info += "@" + "�Ÿ� : " + star.get(num).getDistanceStr() + " ����";
					info += "@" + "��� : " + star.get(num).getBrightnessStr() + " �¾籤��";
					info += "@" + "���� : " + star.get(num).getMassStr() + " ������";
					info += "@" + "ũ�� : " + star.get(num).getSizeStr() + " �¾�ݰ�";
				}
			}
		}
		return info;
	}
	
	// DB�� ����� �༺�� ���¸� ���� ������ ���·� ��ȯ 
	public String BuildPlanet(double mass, double kelvin) {
		String class_type = "";
		
		// ������ 0.04 MJ �̻��̸� �����༺���� �з�
		if( mass >= 0.04f ) {
			class_type = "�����༺";
			if( kelvin >= 1400.0f ) class_type += "@A";
			else if( kelvin >= 800.0f && kelvin < 1400.0f ) class_type += "@B";
			else if( kelvin >= 350.0f && kelvin < 800.0f ) class_type += "@C";
			else if( kelvin >= 150.0f && kelvin < 350.0f ) class_type += "@D";
			else class_type += "@E";
		} else {
			class_type = "�ϼ��༺";
			if( mass >= 0.04f ) class_type += "@F";
			else if( mass >= 0.032f && mass < 0.04f ) class_type += "@G";
			else if( mass >= 0.024f && mass < 0.032f ) class_type += "@H";
			else if( mass >= 0.018f && mass < 0.024f ) class_type += "@I";
			else class_type += "@J";
		}
		
		return class_type;
	}

	// DB�� ����� ���� ���¸� ���� ������ ���·� ��ȯ
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
	
		return class_type + "@" + class_spec + class_grade + class_aff;
	}
}
