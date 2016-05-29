ANTLR4 = java -jar /usr/local/lib/antlr-4.5.3-complete.jar
GRUN = java org.antlr.v4.gui.TestRig

all:
	$(ANTLR4) GenConfig.g4
	javac *.java

run:
	java GenConfigRun

test:all
	$(GRUN) GenConfig file -tokens ../input_configs/grun.genconf