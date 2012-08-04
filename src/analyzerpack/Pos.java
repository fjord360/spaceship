package analyzerpack;

public enum Pos {
	V, // 용언
	N, // 명사 Cnoun
	t, // 용언화접미사 Vsfx : ~"이"다, ~"하"다, ~"받"다, "되"다, "답"다, "스럽"다
	e, // 어미 Eomi
	j, // 조사 Josa
	f, // Pomi 갔다. -> 가"었"디.
	n, // 명사화접미사 "음/기" EList
	c, // 아/어 EList
	W, // 보조용언 XVerb : 도와"주"다, 사랑받아"보"다
	Z, // (Z) : 대명사? 우리, 너희, 그, 그녀, 자네, 누구, 이것, 그것, 저것, 이, 그, 저
	X, // 품사 종류 상관없음
	
	S, // 별 Star
	C  // 별자리 Constellation
}
