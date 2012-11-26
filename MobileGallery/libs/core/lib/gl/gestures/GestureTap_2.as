﻿////////////////////////////////////////////////////////////////////////////////
//
//  IDEUM
//  Copyright 2009-2011 Ideum
//  All Rights Reserved.
//
//  OPEN EXHIBITS SDK (OPEN EXHIBITS CORE)
//
//  File:    	GestureTap_2.as
//
//  Authors:   	Chris Gerber (chris(at)ideum(dot)com),
//				Paul Lacey (paul(at)ideum(dot)com),
//				Matthew Valverde (matthew(at)ideum(dot)com). 
//				
//  NOTICE: OPEN EXHIBITS SDK permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package gl.gestures
{

public class GestureTap_2 extends Gesture_N_Finger_N_Tap
{
	
	override protected function initialize():void
	{
		super.initialize();
		
		pointCount = 2;
		pointExistences = 1;
	}
	
}

}