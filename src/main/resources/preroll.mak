
define mremux
mencoder $< -of lavf -oac pcm -ovc copy -vf harddup -o tmp_$@ && mv -v tmp_$@ $@
endef

VQ?=	-qmin 1 -qmax 27    

VCFLAGS?= $(VQ) $(VCUSTOM) 	-partitions partb8x8+partp8x8+parti8x8+parti4x4+partp4x4        \
        	-mbd rd   									\
        	-me_range 1023 -subq 9 -me_method epzs    


define 4tracks
	$(FFMPEG) $(TINSTART) $(TINLEN)  </dev/null -v 1 $(VTHREADS) -i 	$(firstword $^)		\
	$(VQ)  -f ipod  $(VCFLAGS) $(ENC_OPTS) $(VTHREADS) 	\
	-acodec libfaac -ar 48000 -ab 128K -ac 2			\
	-y tmp_$@ -newvideo -newaudio $(VTHREADS)			\
	-i $(lastword $^) $(VTHREADS) -f ipod $(ENC_OPTS)		\
	&& mv tmp_$@ $@
endef

ALOSSLESS?=pcm_s16le
VLOSSLESS?=ffv1
#VDEBUG=$(shell echo -debug $(echo vis_{qp,vis_mb_type}|tr  ' ' +)  -vismv $( echo {{p,b}f,bb} | tr ' ' + ) )
VCODEC?=-vcodec libx264
SIZE_TAG?=$(lastword $(VSIZE))
VRATE_TAG?=$(lastword $(VRATE))
VBITRATE_TAG?=$(lastword $(VBITRATE))
VCODEC_TAG?=$(lastword $(VCODEC:libx264=h264))
VTAG?=$(SIZE_TAG)@$(VRATE_TAG)@$(VBITRATE_TAG)

TINSTARTTAG=$(lastword $(TINSTART) ) 
TINLENTAG?=$(lastword $(TINLEN) )
TTAG?=$(TINSTARTTAG)@$(TINLENTAG)
VTHREADS?=-threads 4
#VDEBUG=-debug vis_qp+vis_mb_type -vismv pf+bf+bb
TMPF?=nut
PREROLL?=preroll
BARCODE?=qr
INPUT?=in

#%.$(TMPF): %.nut
#	$(mremux)

%.$(TMPF): %.mp4
	$(mremux)

%.$(TMPF): %.flv
	$(mremux)

%.$(TMPF): %.mkv
	$(mremux)

%.$(TMPF): %.m4v
	$(mremux)

%.wav: %.$(TMPF)
	$(FFMPEG)  </dev/null -v 2 -i $<  -ar 48000 -ac 2 -y  tmp_$@ && mv tmp_$@ $@

%@$(VTAG)@$(VLOSSLESS).$(TMPF): %.$(TMPF) 
	$(FFMPEG)   </dev/null -v 1 $(VTHREADS) -i $< \
	$(VTHREADS) $(VSIZE) $(VRATE)	 -vcodec $(VLOSSLESS) 	\
	-acodec libfaac -ar 48000 -ab 128K -ac 2 	\
	-y tmp_$@ 					\
	&& mv tmp_$@ $@

PROJ:=$(shell basename ${PWD})


VENC_OPTS:=$(VSIZE) $(VCODEC) $(VDEBUG) $(VRATE) $(VFLAGS) $(VCHANNELS) $(VOVERLAY) $(VBITRATE)
AENC_OPTS:=$(ASIZE) $(ACODEC) $(ADEBUG) $(ARATE) $(AFLAGS) $(ACHANNELS) $(AOVERLAY) $(ABITRATE)
ENC_OPTS:= $(VENC_OPTS) $(AENC_OPTS)

FFMPEG?=ffmpeg




#
# tested and working 5/3 jn
#
%@qr@$(BARCODE_SIZE).png:
	curl >$@ 'http://chart.apis.google.com/chart?cht=qr&chl=$(BARCODE_TEXT)&chld=L|2&chs=$(BARCODE_SIZE)'


#
# tested and working 5/3 jn
#
%.mp4: %.png
	$(FFMPEG) -loop_input -t $(BARCODE_TIMESPAN) -i $<  $(VCODEC)   -r 1/1 -y $@


#
# tested and working 5/3 jn
#
%.mp4: %.gif
	$(FFMPEG) -loop_input -t $(BARCODE_TIMESPAN) -i $<  $(VCODEC)   -r 1/1 -y $@

%@$(BARCODE)@$(PREROLL)@$(VTAG)@$(VLOSSLESS).$(TMPF): %@$(PREROLL)@$(VTAG)@$(VLOSSLESS).$(TMPF) %@$(BARCODE)@$(BARCODE_SIZE).mp4
	$(FFMPEG)  -v 1 -i $<  -vfilters "[in]setpts=PTS-STARTPTS,[T1]overlay=$(BARCODE_X):$(BARCODE_Y)[out];movie=000:mp4:$(lastword $^),setpts=PTS-STARTPTS[T1]"  -vcodec $(VLOSSLESS) -acodec $(ALOSSLESS) -y tmp_$@ && mv -b tmp_$@ $@


%@$(BARCODE)@$(VTAG)@$(VCODEC_TAG).mp4: %@$(INPUT)@$(VTAG)@$(VLOSSLESS).$(TMPF)  %@$(BARCODE)@$(PREROLL)@$(VTAG)@$(VLOSSLESS).$(TMPF)
	$(4tracks)

%@$(VTAG)@$(VCODEC_TAG).mp4: %@$(INPUT)@$(VTAG)@$(VLOSSLESS).$(TMPF) %@$(PREROLL)@$(VTAG)@$(VLOSSLESS).$(TMPF)
	$(4tracks)

%_track1.mp4: %.mp4
	MP4Box -v $< -single 1

%_track2.mp4: %.mp4
	MP4Box -v $< -single 2

%_track3.mp4: %.mp4
	MP4Box -v $< -single 3

%_track4.mp4: %.mp4
	MP4Box -v $< -single 4

%@final@$(VTAG)@$(VCODEC_TAG).mp4: %@$(VTAG)@$(VCODEC_TAG)_track1.mp4 		\
			           %@$(VTAG)@$(VCODEC_TAG)_track2.mp4 		\
		   	           %@$(VTAG)@$(VCODEC_TAG)_track3.mp4		\
		    	           %@$(VTAG)@$(VCODEC_TAG)_track4.mp4
	MP4Box -new tmp_$@ 							\
	$(shell echo -cat\ $*@$(VTAG)@$(VCODEC_TAG)_track{3,4,2,1}.mp4 ) && 	\
	mv tmp_$@ $@


%@$(BARCODE)@final@$(VTAG)@$(VCODEC_TAG).mp4: %@$(BARCODE)@$(VTAG)@$(VCODEC_TAG)_track1.mp4 	\
			           %@$(BARCODE)@$(VTAG)@$(VCODEC_TAG)_track2.mp4 		\
		   	           %@$(BARCODE)@$(VTAG)@$(VCODEC_TAG)_track3.mp4		\
		    	           %@$(BARCODE)@$(VTAG)@$(VCODEC_TAG)_track4.mp4
	MP4Box -new tmp_$@ $(shell echo -cat\ $*@$(VTAG)@$(VCODEC_TAG)_track{3,4,2,1}.mp4 ) && 		\
	mv tmp_$@ $@

.PHONY: badge backdrop all

all: badge
 
badge: $(PROJ)@$(BARCODE)@final@$(VTAG)@$(VCODEC_TAG).mp4

backdrop: $(PROJ)@final@$(VTAG)@$(VCODEC_TAG).mp4

BARCODE_TEXT?=HTTP://HIDEFTVADS.COM
BARCODE_SIZE?=64
BARCODE_X?=0
BARCODE_Y?=0
POSTER_X?=0
POSTER_Y?=0
#safe length to do overlays 
BARCODE_TIMESPAN=120
