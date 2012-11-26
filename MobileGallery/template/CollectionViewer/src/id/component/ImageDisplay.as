////////////////////////////////////////////////////////////////////////////////
//
//  OPEN EXHIBITS
//  Copyright 2010-2011 OPEN EXHIBITS
//  All Rights Reserved.
//
//  ImageDisplay
//
//  File:     ImageDisplay.as
//  Author:    Mattthew Valverde (matthew(at)ideum(dot)com)
//
//  NOTICE: OPEN EXHIBITS permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package id.component
{
	import flash.events.Event;
	import flash.display.DisplayObject;
	import flash.display.DisplayObjectContainer;
	import flash.display.Bitmap;
	import flash.display.Loader;
	import flash.display.Sprite;
	import flash.display.Shape;
	import id.core.ApplicationGlobals;
	import id.core.TouchSprite;
	import id.core.TouchComponent;
	import id.element.BitmapLoader;
	import id.element.Outline;
	import id.element.Graphic;
	import id.element.Parser;
	import gl.events.TouchEvent;
	import gl.events.GestureEvent;
	import com.greensock.TweenLite;
	import flash.utils.Timer;
	import flash.events.TimerEvent;

	import caurina.transitions.Tweener;
	import flash.utils.Timer;

	public class ImageDisplay extends TouchComponent
	{
		private var thumb:BitmapLoader;//the image
		private var ghostMedia:BitmapLoader;
		private var image:BitmapLoader;
		private var outline:Outline;//outline of the image
		private var blocker:Graphic;

		private var _id:int;
		private var intialize:Boolean;
		private var globalScale:Number;
		private var scale:Number;
		private var imagesNormalize:Number;
		private var maxScale:Number;
		private var minScale:Number;
		private var backgroundOutlineStyle:Object;
		private var url:String;
		private var dragGesture:Boolean = true;
		private var rotateGesture:Boolean = true;
		private var scaleGesture:Boolean = true;
		private var doubleTapGesture:Boolean = true;
		private var flickGesture:Boolean = true;
		private var stageWidth:Number;
		private var stageHeight:Number;
		private var disposeArray:Array = new Array();

		private var flickFriction:Number = .9;
		private var flickDx:Number;
		private var flickDy:Number;

		private var showLarge:Boolean = false;

        private var imgIndex:String;
		/**
		 *
		 * The Constructor.
		 * <pre>
		 * <strong>var imageDisplay:ImageDisplay = new ImageDisplay();
		 * addChild(imageDisplay);</strong></pre>
		 *
		 */
		public function ImageDisplay()
		{
			super();
			blobContainerEnabled = true;
			visible = false;
		}

		/**
		 *
		 * The Disposal method for the module. It will clean out and nullify all children.
		 * <pre>
		 * <strong>imageDisplay.Dispose();</strong></pre>
		 *
		 */
		/*override public function Dispose():void
		{
		}*/

		override public function get id():int
		{
			return _id;
		}

		override public function set id(value:int):void
		{
			_id = value;
			createUI();
			commitUI();
		}

		override protected function createUI():void
		{
			// Style
			globalScale = Parser.settings.GlobalSettings.globalScale;
			scale = Parser.settings.GlobalSettings.scale;
			imagesNormalize = Parser.settings.GlobalSettings.imagesNormalize;
			maxScale = Parser.settings.GlobalSettings.maxScale;
			minScale = Parser.settings.GlobalSettings.minScale;
			stageWidth = ApplicationGlobals.application.stage.stageWidth;
			stageHeight = ApplicationGlobals.application.stage.stageHeight;

            imgIndex = Parser.settings.Content.Source[id].imgIndex;

			if (! maxScale || ! minScale)
			{
				maxScale = 2;
				minScale = .5;
			}

			backgroundOutlineStyle = Parser.settings.BackgroundOutline;

			// Data
			url = Parser.settings.Content.Source[id].url;

			// Objects
			thumb = new BitmapLoader();
			ghostMedia = new BitmapLoader();
			image = new BitmapLoader();
			outline = new Outline();
			blocker = new Graphic();

			// Add Children
			addChild(thumb);
			addChild(ghostMedia);
			addChild(outline);

			// Add Event Listeners
			ghostMedia.addEventListener(TouchEvent.TOUCH_DOWN, touchDownHandler);
			ghostMedia.addEventListener(TouchEvent.TOUCH_UP, touchUpHandler);
			//ghostMedia.addEventListener(TouchEvent.TOUCH_TAP, tapHandler);

			//thumb.addEventListener(TouchEvent.TOUCH_TAP, tapHandler);
		}

		override protected function commitUI():void
		{
			if (scale)
			{
				thumb.scale = scale;
				ghostMedia.scale = scale;
			}

			if (imagesNormalize)
			{
				thumb.pixels = imagesNormalize;
				ghostMedia.pixels = imagesNormalize;
			}

			outline.styleList = backgroundOutlineStyle;

			thumb.url = url;
			ghostMedia.url = url;
			image.url = url;
			image.pixels = thumb.pixels * 4;
		}

		override protected function layoutUI():void
		{
			if (! intialize)
			{
				if (globalScale)
				{
					scaleX = globalScale;
					scaleY = globalScale;
				}
				intialize = true;
			}
			
			blocker.alpha = 0;
			blocker.fillColor1 = 0x00000000;//black
			blocker.width = 1600;
			blocker.height = 900;

			image.x = 800 - image.x/2;
			image.y = 450 - image.y/2;
			image.alpha = 0;

			ghostMedia.alpha = 0;

			outline.width = thumb.width;
			outline.height = thumb.height;
			outline.x = thumb.x;
			outline.y = thumb.y;

			width = thumb.width;
			height = thumb.height;

			visible = true;
		}

		//not using it for now
		override protected function updateUI():void
		{
			image.width *= scaleX;
			image.height *= scaleY;
		}

		private function touchDownHandler(event:TouchEvent):void
		{
			if( outline.color != 0x00FF00 )
			{
    			 addChild(ghostMedia);
    			 ghostMedia.alpha = 0.5;
    			 Tweener.addTween(thumb, { alpha: 0.5, time: 1 } );
    			 parent.setChildIndex(this as DisplayObject,parent.numChildren-1);
    			 ghostMedia.startTouchDrag(-1);
			}
		}

		private function touchUpHandler(event:TouchEvent):void
		{
			ghostMedia.stopTouchDrag(-1);
			if (outline.color != 0x00FF00)
			{
				var ghostX = ghostMedia.x + ghostMedia.width / 2;//center X of ghost image
				var ghostY = ghostMedia.y + ghostMedia.height / 2;//center Y of ghost image
				if (ghostY > 318 && ghostY < 566 && ghostX > 1312 && ghostX < 1560)
				{
					//fade out ghostMedia
					Tweener.addTween(ghostMedia, { alpha: 0, time: 1 } );
					//wait until ghostMedia has faded out
					var timer:Timer = new Timer(1000,1);
					timer.addEventListener( TimerEvent.TIMER_COMPLETE, 
					 function()
					 { 
						 ghostMedia.x = thumb.x;
					     ghostMedia.y = thumb.y;
					 } );
					timer.start();
					//glow Outline
					changeOutline();
				}
				else
				{
					Tweener.addTween(thumb, { alpha: 1, time: 1 } );
					ghostMedia.x = thumb.x;
					ghostMedia.y = thumb.y;
					ghostMedia.alpha = 0;
					addChild( outline );
				}
			}
		}

		private function dragHandler(e:GestureEvent):void
		{
			if (showLarge)
			{
				image.x +=  e.dx;
				image.y +=  e.dy;
			}
		}

		private function tapHandler(event:TouchEvent):void
		{
			if (!showLarge)
			{
				parent.setChildIndex(this as DisplayObject,parent.numChildren-1); //bring image forward
				addChild( blocker );
				addChild( image );
				Tweener.addTween(blocker, { alpha: 0.5, time: 1 } );
				Tweener.addTween(image, { alpha: 1, time: 1 } );
				image.addEventListener(TouchEvent.TOUCH_TAP, tapHandler);
				image.addEventListener(GestureEvent.GESTURE_SCALE, scaleGestureHandler);
				showLarge = true;
			}
			else
			{
				Tweener.addTween(blocker, { alpha: 0, time: 1 } );
				Tweener.addTween(image, { alpha: 0, time: 1 } );
				//wait until ghostMedia has faded out
				var timer:Timer = new Timer(500,1);
				timer.addEventListener( TimerEvent.TIMER_COMPLETE, 
			    function()
			    { 
					removeChild( image );
					removeChild( blocker );
			    } );
				timer.start();
				image.removeEventListener(TouchEvent.TOUCH_TAP, tapHandler);
				image.removeEventListener(GestureEvent.GESTURE_SCALE, scaleGestureHandler);
				showLarge = false;
			}
		}

		private function scaleGestureHandler(event:GestureEvent):void
		{
			scaleX += event.value;
			scaleY += event.value;

			scaleY = scaleY > Number(maxScale) ? Number(maxScale):scaleY < Number(minScale) ? Number(minScale):scaleY;
			scaleX = scaleX > Number(maxScale) ? Number(maxScale):scaleX < Number(minScale) ? Number(minScale):scaleX;

			updateUI();
		}

		//change outline color
		public function changeOutline():void
		{
			if (outline.color != 0x00FF00)
			{
				outline.size = 3;
				outline.color = 0x00FF00;//green
				outline.x = thumb.x;
				outline.y = thumb.y;

                Main.sendTxt( imgIndex );
			}
			else
			{
				outline.size = 1;
				outline.color = 0xFFFFFFFF;//white
				outline.x = thumb.x;
				outline.y = thumb.y;
			}
		}

		public function reset():void
		{
			outline.size = 1;
			outline.color = 0xFFFFFFFF;
			outline.x = thumb.x;
			outline.y = thumb.y;
			Tweener.addTween(thumb, { alpha: 1, time: 1 } );
		}

	}//ImageDisplay()
}//package