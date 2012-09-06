package spacepack;

public class Star {
	private String name;			// �̸�
	private String type;			// Ÿ��
	private String form;			// ����
	private String[] ascension;		// ����
	private String[] declination;	// ����
	private String distance;		// �Ÿ�
	private String brightness;		// ���
	private String size;			// ũ��
	private String mass;			// ����
	private String kelvin;			// �µ�
	private String mother;			// ����׼�
	private String semimajor;		// �˵��Ÿ�
	private String period;			// �����ֱ�
	private String inclination;		// ��簢
	private String texture;			// �ؽ�������
	private String disk;			// ��
	
	// ������
	public Star(String Name) {
		name = Name;
		type = "";
		form = "";
		ascension = new String[3];
		ascension[0] = "0";
		ascension[1] = "0";
		ascension[2] = "0";
		declination = new String[3];
		declination[0] = "0";
		declination[0] = "0";
		declination[0] = "0";
		distance = "0";
		brightness = "0";
		size = "0";
		mass = "0";
		kelvin = "0";
		mother = "";
		semimajor = "0";
		period = "0";
		inclination = "0";
		texture = "None";
		disk = "0";
	}
	
	// �̸� ����, ����
	public void setName(String Name) {
		name = Name;
	}
	public String getName() {
		return name;
	}
	
	// Ÿ�� ����, ����
	public void setType(String Type) {
		type = Type;
	}
	public String getType() {
		return type;
	}
	
	// ���� ����, ����
	public void setForm(String Form) {
		form = Form;
	}
	public String getForm() {
		return form;
	}
	
	// ���� ����, ����
	public void setAsc(String[] Asc) {
		ascension = Asc;
	}
	public void setAsc(int num, String Asc) {
		ascension[num] = Asc;
	}
	public String getAscStr() {
		String value = ascension[0] + "h ";
		value += ascension[1] + "m ";
		value += ascension[2] + "s";
		return value;
	}
	public double getAscCoord() {
		return AuToCoord(ascension);
	}
	
	// ���� ����, ����
	public void setDec(String[] Dec) {
		declination = Dec;
	}
	public void setDec(int num, String Dec) {
		declination[num] = Dec;
	}
	public String getDecStr() {
		String value = declination[0] + "�� ";
		value += declination[1] + "�� ";
		value += declination[2] + "�� ";
		return value;
	}
	public double getDecCoord() {
		return AuToCoord(declination);
	}
	
	// �Ÿ� ����, ����
	public void setDistance(String Distance) {
		distance = Distance;
	}
	public double getDistance() {
		if( distance == null ) return 0.0f;
		else return ParseValue(distance);
	}
		
	public String getDistanceStr() {
		double value = ParseValue(distance);
		if( value <= 0.0f ) return "0.0";
		return (String.format("%.2f", value));
	}
	
	// ��� ����, ����
	public void setBrightness(String Brigthness) {
		brightness = Brigthness;
	}
	public double getBrightness() {
		if( brightness == null ) return 0.0f;
		else return ParseValue(brightness);
	}
	public String getBrightnessStr() {
		double value = ParseValue(brightness);
		if( value <= 0.0f ) return "0.0";
		return (String.format("%.2f", value));
	}
	
	// ũ�� ����, ����
	public void setSize(String Size) {
		size = Size;
	}
	public double getSize() {
		if( size == null ) return 0.0f;
		else return ParseValue(size);
	}
	public String getSizeStr() {
		if( size == null ) return "0.0f";
		else {
			double value = ParseValue(size);
			if( value <= 0.0f ) return "0.0";
			else return (String.format("%.2f", value));
		}
	}
	public String getSizeCalc() {
		if( size == null ) return "0.0";
		else {
			double value = ParseValue(size);
			if( mother != null ) {
				if( mother.equals("�¾�") ) return String.format("%.2f", value / 2.0f);
				else return String.format("%.2f", value);
			}
			else if( type.equals("satellite") ) return "0.0";
			else return String.format("%.2f", value);
		}
	}
	public String getSizeREtoKMStr() {
		double parse = ParseValue(size);
		if( parse <= 0.0f ) return "0.0";
		else return (String.format("%.1f", parse*6378.0f)) ;
	}
	
	// ���� ����, ����
	public void setMass(String Mass) {
		mass = Mass;
	}
	public double getMass() {
		if( mass == null ) return 0.0f;
		else return ParseValue(mass);
	}
	public double getMassEJtoMJ() {
		// �������� -> ������ ��ȯ
		if( mass == null ) return 0.0f;
		else return ParseValue(mass) / 317.8f;
	}
	public String getMassStr() {
		double value = ParseValue(mass);
		if( value <= 0.0f ) return "0.0";
		return (String.format("%.2f", value));
	}
	
	// �µ� ����, ����
	public void setKelvin(String Kelvin) {
		kelvin = Kelvin;
	}
	public double getKelvin() {
		if( kelvin == null ) return 0.0f;
		else return ParseValue(kelvin);
	}
	public String getKelvinStr() {
		double value = ParseValue(kelvin);
		if( value <= 0.0f ) return "0.0";
		return (String.format("%.1f", value));
	}
	
	// ����׼� ����, ����
	public void setMother(String Mother) {
		mother = Mother;
	}
	public String getMotherStr() {
		if( mother == null ) return "None";
		return mother;
	}
	
	// �˵��Ÿ� ����, ����
	public void setSemimajor(String Semimajor) {
		semimajor = Semimajor;
	}
	public double getSemimajor() {
		if( semimajor == null ) return 0.0f;
		else return ParseValue(semimajor);
	}
	public double getSemimajorCalc() {
		if( semimajor == null ) return 0.0f;
		else {
			double value = ParseValue(semimajor);
			if( getType().equals("satellite") ) {
				String stringValue = String.format("%.3f", value * 0.0000001f);
				return ParseValue(stringValue);
			}
			else return value;
		}
	}
	public String getSemimajorStr() {
		return semimajor;
	}
	
	// �����ֱ� ����, ����
	public void setPeriod(String Period) {
		period = Period;
	}
	public double getPeriod() {
		if( period == null ) return 0.0f;
		else return ParseValue(period);
	}
	public String getPeriodStr() {
		return period;
	}
	public String getPeriodConvertStr() {
		double value = ParseValue(period);
		if( value >= 365.0f ) return (String.format("%.2f", value / 365.25641f)) + " ��";
		else return (String.format("%.2f", value)) + " ��";
	}
	
	// ��簢 ����, ����
	public void setInclination(String Inclination) {
		inclination = Inclination;
	}
	public double getInclination() {
		if( inclination == null ) return 0.0f;
		else return ParseValue(inclination);
	}
	public String getInclinationStr() {
		return inclination;
	}
	
	// �ؽ��� ����, ����
	public void setTexture(String Texture) {
		texture = Texture;
	}
	public String getTexture() {
		if( texture == null ) return "None";
		else return texture;
	}
	
	// �� ����, ����
	public void setDisk(String Disk) {
		disk = Disk;
	}
	public String getDisk() {
		if( disk == null ) return "0";
		else return disk;
	}
	
	// ����, ������ ��ǥ���·� ����
	public double AuToCoord(String[] au) {
		double parse = ParseValue(au[0]);
		double coord = 0.0f;
		if( parse > 0.0f ) coord = (parse * 100.0f);
		coord += ParseValue(au[1]);
		return coord;
	}
	
	// ��Ʈ�� -> ���� (��հ��� ��������)
	public double ParseValue(String text) {
		double value = 0.0f;
		if( text == null ) text = "0";
		if( text.indexOf("~") > 0 ) {
			String[] split = text.split("~");
			double value1 = ParseValue(split[0]);
			double value2 = ParseValue(split[1]);
			value = (value1 + value2) / 2.0f;
		}
		else if( text.indexOf("��") > 0 ) {
			String[] split = text.split("��");
			value = ParseValue(split[0]);
		}
		else {
			char[] textArray = text.toCharArray();
			String parseString = "";
			for( int i = 0 ; i < textArray.length ; i++ ) {
				if( Character.isDigit(textArray[i]) ) parseString += textArray[i];
				if( i == 0 && textArray[i] == '-' ) parseString += textArray[i];
				if( i != 0 && textArray[i] == '.' ) parseString += textArray[i];
			}
			if( parseString.length() == 0 ) parseString = "0";
			value = Double.parseDouble(parseString);
		}
		
		return value;
	}
}