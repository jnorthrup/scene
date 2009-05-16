package png;/*
* Animation.java -- �A�j���V�����E�f�[�^�̂��߂̒��ۃN���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.awt.*;

abstract class Animation {
	abstract public void paintFrame(Graphics g);
	abstract public void fetchNextFrame();
	abstract public long getInterFrameDelay();
	abstract public boolean isEndOfAnimation();
}
