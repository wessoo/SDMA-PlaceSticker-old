package 
{
	import flash.display.StageScaleMode;
	import flash.display.StageAlign;
	import flash.display.StageDisplayState;
	import flash.ui.Mouse;
	import id.core.Application;
	import id.core.ApplicationGlobals;
	import id.template.CollectionViewer;
	import id.core.TouchSprite;
	import gl.events.TouchEvent;
	import flash.utils.Timer;
	import flash.events.TimerEvent;
	import id.element.Outline;
	
	import flash.events.Event;
    import flash.net.Socket;
    import flash.errors.IOError;
	
	import caurina.transitions.Tweener;
	
	public class Main extends Application
	{
		private var createBtnCtnr:TouchSprite;
		private var resetBtnCtnr:TouchSprite;
		private var aboutInfoCtnr:TouchSprite;

        private var doneBtnCtnr: TouchSprite;
        private var cancelBtnCtnr: TouchSprite;

        private var cancelDoneY: Number;
        private var createPopY:Number;
		
		public var collectionViewer:CollectionViewer;

		public var outline:Outline;
		
		public var shaderCtnr:TouchSprite;
		
		private var aboutOn:Boolean = false;
		private var aboutOnY:Number;
		private var aboutOffY:Number;
		
		private var cancelDoneCenterY:Number;
		
		private var popUpY:Number;

        public static var ip:String;
		
		public function Main()
		{
			settingsPath="library/data/Application.xml";
			
			stage.scaleMode=StageScaleMode.EXACT_FIT;
			stage.displayState=StageDisplayState.FULL_SCREEN;
			stage.align = StageAlign.TOP_LEFT;
		}

		override protected function initialize():void
		{
            var delay:Number = ApplicationGlobals.dataManager.data.Template.delay;

            var timer:Timer = new Timer(1000, delay);
            timer.addEventListener(TimerEvent.TIMER_COMPLETE, timerHandler);
            timer.start();

            function timerHandler(e:TimerEvent):void
            {
               	timer.removeEventListener(TimerEvent.TIMER_COMPLETE,timerHandler);
                removeChild( blocker );
               	loadUI();
            }

            ip = ApplicationGlobals.dataManager.data.Template.ip;
		}
		
		public function loadUI():void
		{
			//shader
			shaderCtnr = new TouchSprite();
			shaderCtnr.addChild(shader);
			shaderCtnr.alpha = 0;
			shaderCtnr.x = shaderCtnr.y = 0;
			
			//create button
			createBtnCtnr = new TouchSprite();
			createBtnCtnr.addChild(createBtn);
			createBtnCtnr.addEventListener(TouchEvent.TOUCH_DOWN, createDown);
			createBtnCtnr.addEventListener(TouchEvent.TOUCH_UP, createUp);
			addChild(createBtnCtnr);
			
			//reset button
			resetBtnCtnr = new TouchSprite();
			resetBtnCtnr.addChild(resetBtn);
			resetBtnCtnr.addEventListener(TouchEvent.TOUCH_DOWN, resetDown);
			resetBtnCtnr.addEventListener(TouchEvent.TOUCH_UP, resetUp);
			addChild(resetBtnCtnr);

			//info panel
			aboutInfoCtnr = new TouchSprite();
			aboutInfoCtnr.addChild(aboutInfo);
			aboutInfoCtnr.addEventListener(TouchEvent.TOUCH_TAP, aboutPanel);
			addChild(aboutInfoCtnr);
			aboutOnY = stage.height/2-3*(aboutInfo.height/4);
			aboutOffY = aboutInfo.y;

            //done button
			doneBtnCtnr = new TouchSprite();
			doneBtnCtnr.addChild(doneBtn);
			doneBtnCtnr.addEventListener(TouchEvent.TOUCH_DOWN, doneDown);
			doneBtnCtnr.addEventListener(TouchEvent.TOUCH_UP, doneUp);
			addChild(doneBtnCtnr);

            //cancel button
			cancelBtnCtnr = new TouchSprite();
			cancelBtnCtnr.addChild(cancelBtn);
			cancelBtnCtnr.addEventListener(TouchEvent.TOUCH_DOWN, cancelDown);
			cancelBtnCtnr.addEventListener(TouchEvent.TOUCH_UP, cancelUp);
			addChild(cancelBtnCtnr);

            createPopY = createPop.y;
            cancelDoneY = cancelBtnCtnr.y;
			
			popUpY = ( stage.stageHeight - createPop.height )/2;
			cancelDoneCenterY = popUpY - createPopY;

			collectionViewer = new CollectionViewer();
			addChild(collectionViewer);
		}
		
		//create button touch up down
		private function createDown(e:TouchEvent):void 
		{
			createBtn.gotoAndStop(2);
		}
		
		private function createUp(e:TouchEvent):void 
		{
			createBtn.gotoAndStop(1);
            showCreatePop();
		}

        private function showCreatePop():void
        {
            shadeOn();
            addChild( createPop );
            addChild( doneBtnCtnr );
            addChild( cancelBtnCtnr );

            Tweener.addTween( doneBtnCtnr, {y: cancelDoneCenterY, time:1} );
            Tweener.addTween( cancelBtnCtnr, {y: cancelDoneCenterY, time:1} );
            Tweener.addTween( createPop, {y: popUpY, time:1} );
        }

        private function hideCreatePop():void
        {
            shadeOff();
            Tweener.addTween( doneBtnCtnr, {y: cancelDoneY, time:1} );
            Tweener.addTween( cancelBtnCtnr, {y: cancelDoneY, time:1} );
            Tweener.addTween( createPop, {y: createPopY, time:1} );
        }

        //done button touch up down
		private function doneDown(e:TouchEvent):void 
		{
			doneBtn.gotoAndStop(2);
		}
		
		private function doneUp(e:TouchEvent):void 
		{
			doneBtn.gotoAndStop(1);
            hideCreatePop();
            sendTxt( "create" );
            for(var i:int = 0; i < 25; ++i){
				var image:Object = collectionViewer.getChildAt(i+2); //child 1 is bitmaploader, 2 is imageViewer, 3 and on are the images
				image.reset();
			}
		}

        //create button touch up down
		private function cancelDown(e:TouchEvent):void 
		{
			cancelBtn.gotoAndStop(2);
		}
		
		private function cancelUp(e:TouchEvent):void 
		{
			cancelBtn.gotoAndStop(1);
            hideCreatePop();
		}
		
		//reset button touch up down
		private function resetDown(e:TouchEvent):void
		{
			resetBtn.gotoAndStop(2);
		}
		
		private function resetUp(e:TouchEvent):void 
		{
			resetBtn.gotoAndStop(1);
			for(var i:int = 0; i < 25; ++i){
				var image:Object = collectionViewer.getChildAt(i+2); //child 1 is bitmaploader, 2 is imageViewer, 3 and on are the images
				image.reset();
			}
            sendTxt( "0" );
		}
		
		//animate about panel
		private function aboutPanel(e:TouchEvent):void
		{
			if( !aboutOn )
			{
				aboutOn = true;
				shadeOn();
				addChild(aboutInfoCtnr);//bring aboutInfoCtnr to the front
				Tweener.addTween(aboutInfo, { y: aboutOnY, time: 1 } );
			}
			else
			{
				aboutOn = false;
				shadeOff();
				Tweener.addTween(aboutInfo, { y: aboutOffY, time: 1 } );
			}
		}
		
		public function shadeOn():void
		{
			addChild(shaderCtnr);
			Tweener.addTween(shaderCtnr, { alpha: 0.5, time: 0.5 } );
		}
		
		public function shadeOff():void 
		{
			Tweener.addTween(shaderCtnr, { alpha: 0, time: 0.5 } );
			removeChild(shaderCtnr);
		}

        public static function sendTxt( imgIndex: String ):void
        {
           	var socket:Socket = new Socket();
           	socket.addEventListener(Event.CONNECT, onConnect);
			
	        if( !socket.connected )
	        {
   		         try 
		         {
   		         	socket.connect(ip, 8800);
            	 } 
		         catch (e:IOError) 
		         {
    		         trace( IOError );
                 }
 	        }
  
  	        function onConnect(e:Event):void 
	        {
		        socket.writeUTF( imgIndex );
            	socket.flush();
	        }
        }
		
	}//main class
}//package
