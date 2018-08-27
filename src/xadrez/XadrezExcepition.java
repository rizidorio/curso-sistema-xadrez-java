package xadrez;

import tabuleirojogo.TabuleiroException;

public class XadrezExcepition extends TabuleiroException {
	private static final long serialVersionUID = 1L;
	
	public XadrezExcepition(String msg) {
		super(msg);
	}

}
