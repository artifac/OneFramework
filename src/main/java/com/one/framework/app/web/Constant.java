package com.one.framework.app.web;

public class Constant {
    public static final String EXTRA_INFILE = "input_file";
    public static final String EXTRA_OUTFILE = "output_file";
    public static final String EXTRA_SAMPLE = "sample"; //only set 16000 (default)
    
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_PID = "pid";
    public static final String EXTRA_APP_PARAM = "app_param"; //opt
    
    public static final String EXTRA_NEED_CORPUS_STORE = "corpus_store";

    public static final String EXTRA_VAD_MAX_SPEECH_END_SILENCE_LENGTH = "vad_max_speech_end_silence_length";
    
    public static final String EXTRA_SOUND_START = "sound_start";
    public static final String EXTRA_SOUND_END = "sound_end";
    public static final String EXTRA_SOUND_SUCCESS = "sound_success";
    public static final String EXTRA_SOUND_ERROR = "sound_error";
    public static final String EXTRA_SOUND_CANCEL = "sound_cancel";
    
    public static final int EVENT_ERROR_TYPE = 10002;
    public static final int EVENT_CORPUS_TYPE = 10003;

    //用来区分新老协议中针对commonPay是否要多包一层。
    public static final String FUSION_PACKAGED = "fusion_packaged";
}
