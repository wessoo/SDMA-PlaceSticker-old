package id.template
{
	import flash.utils.getDefinitionByName;
	import flash.display.DisplayObject;
	import flash.utils.Timer;
	import flash.events.TimerEvent;
	import id.core.ApplicationGlobals;
	import id.core.TouchComponent;
	import id.element.TextDisplay;
	import gl.events.TouchEvent;
	import com.greensock.TweenLite;
	import flash.utils.Dictionary;
	import flash.display.Sprite;

	import id.module.ImageViewer; ImageViewer;
	
	import id.element.BitmapLoader;

	public class CollectionViewer extends TouchComponent
	{
		private var templates:Object;
		private var _id:int;
		private var count:int;
		private var moduleClass:Class;
		private var module:DisplayObject;
		public var loadingTime:Timer;
		private var layoutCalled:Boolean;
		private var secondTime:Boolean;
		private var templateLoaded:Boolean;
		public var objects:Array = new Array();
		private var isTemplateLoaded:Boolean;
		private var moduleDictionary:Dictionary = new Dictionary();
		private var moduleID:Array = new Array();
		
		private var _moduleName:String="";
		private var stageWidth:Number;
		private var stageHeight:Number;
		
		private var background:Sprite;
		private var bitmap:BitmapLoader;
		
		private var moduleNameArray:Array = new Array();
		private var backgroundUrl:String;

		public function CollectionViewer()
		{
			super();
			templates=ApplicationGlobals.dataManager.data.Template;
			createUI();
			commitUI();
		}
		
		override public function get id():int
		{
			return _id;
		}
		override public function set id(value:int):void
		{
			_id=value;
		}
		
		override public function get moduleName():String
		{
			return _moduleName;
		}
		override public function set moduleName(value:String):void
		{
			_moduleName=value;
		}

		override protected function createUI():void
		{
			
			stageWidth=ApplicationGlobals.application.stage.stageWidth;
			stageHeight=ApplicationGlobals.application.stage.stageHeight;

			background = new BitmapLoader();
			bitmap = new BitmapLoader();
			
			background.addChild(bitmap)
			addChild(background);

			loadingTime=new Timer(50);
			loadingTime.addEventListener(TimerEvent.TIMER, updateLoadingText);
		}

		override protected function commitUI():void
		{
			//background of flash
			//backgroundUrl = templates.background;
			callModuleClass();
			
			if(backgroundUrl)
			{
				bitmap.url = backgroundUrl;
				bitmap.width = stageWidth;
				bitmap.height = stageHeight;
			}
		}

		override protected function layoutUI():void
		{
			layoutCalled=true;
			
			var moduleObject:Object = getModule(moduleDictionary);
				
			addToObjectsArray(moduleObject.displayObject);
			
			if(isTemplateLoaded)
			{				
				addModulesToStage();
			}
		}

		override protected function updateUI():void
		{			
			var moduleObject:Object = getModule(moduleDictionary);
			moduleObject.callNewObject(id);
		}
		
		private function updateLoadingText(event:TimerEvent):void
		{
			if(secondTime)
			{
				loadingTime.reset();
				loadingTime.stop();
				
				count++;
				
				if (count==templates.module.length())
				{
					isTemplateLoaded=true;
					loadingTime.removeEventListener(TimerEvent.TIMER, updateLoadingText);
					loadingTime = null;
					addModulesToStage();
					
					return;
				}
				else
				{
					callModuleClass();
				}				
				return;
			}
			
			if (layoutCalled)
			{
				loadingTime.reset();
				loadingTime.start();
			}
			else
			{
				loadingTime.reset();
				loadingTime.stop();
				loadingTime.start();
			}
			secondTime=true;
		}

		private function callModuleClass():void
		{
			layoutCalled=false;
			secondTime=false;
			loadingTime.start();

			moduleClass=getDefinitionByName("id.module."+templates.module[count]) as Class;
			
			module = new moduleClass();
			addChild(module);
			
			moduleName=templates.module[count];
			moduleDictionary[module] = templates.module[count];
		}
		
		/***XXX picture locations ***/
		private function addModulesToStage():void
		{
			var locY:int = 120-70.2;
			var locX:int = 163-95;
			for (var i:int=0; i<objects.length; i++)
			{
				addChild(objects[i]);
				objects[i].x = 0;
				objects[i].y = 0;
				
				for( var j:int=0; j<objects[i].numChildren; j++ )
				{
					objects[i].getChildAt( j ).x=locX;
					objects[i].getChildAt( j ).y=locY;
				}
				//objects[i].x=locX;/*Math.random()*ApplicationGlobals.application.stage.stageWidth;*/
				locX = locX + 246;
				//objects[i].y=locY;/*Math.random()*ApplicationGlobals.application.stage.stageHeight;*/
				TweenLite.to(objects[i], 2, { alpha:1});
				if( i!= 0 && (i+1)%5 == 0 ) 
				{
					locY = locY + 165;
					locX = 163-95;
				}
			}
			objects=[];
		}
		
		private function addToObjectsArray(value:Array):void
		{
			for (var i:int=0; i<value.length; i++)
			{
				objects.push(value[i]);
			}
		}
		
		private function getModule(value:Dictionary):Object
		{
			var moduleObject:Object = new Object();
			
			for (var object:Object in value)
			{
				if(value[object]==moduleName)
				{
					moduleObject=object;
				}
			}
			
			return moduleObject;
		}
	}//end class
}//end id_template