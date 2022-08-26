package com.justin;

public class QString {
    private String sender;
    private String recipient;
    private String subject;
    private String filename;
    private String phrase;
    private String folder;
    private String after;
    private String before;

    public QString(
        String _sender, String _recipient, String _subject, 
        String _filename, String _phrase, String _folder, 
        String _after, String _before){
        
        sender = _sender;
        recipient = _recipient;
        subject = _subject; 
        filename = _filename; 
        phrase = _phrase;
        folder = _folder;
        after = _after; 
        before = _before;
    }

    static class QBuilder {
        private String _sender = "";
        private String _recipient = "";
        private String _subject = "";
        private String _filename = "";
        private String _phrase = "";
        private String _folder = "";
        private String _after = "";
        private String _before = "";
        
        public QBuilder(){
            //empty init
        }
    
        public QString buildQ(){
            return new QString(_sender, _recipient, _subject, _filename, _phrase, _folder, _after, _before);
        }
    
        public QBuilder sender(String _sender){
            this._sender = _sender;
            return this;
        }
    
        public QBuilder recipient(String _recipient){
            this._recipient = _recipient;
            return this;
        }
    
        public QBuilder subject(String _subject){
            this._subject = _subject;
            return this;
        }
    
        public QBuilder filename(String _filename){
            this._filename = _filename;
            return this;
        }
    
        public QBuilder phrase(String _phrase){
            this._phrase = _phrase;
            return this;
        }
    
        public QBuilder folder(String _folder){
            this._folder = _folder;
            return this;
        }
    
        public QBuilder after(String _after){
            this._after = _after;
            return this;
        }
    
        public QBuilder before(String _before){
            this._before = _before;
            return this;
        }
    }

}
