package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph._
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.ui.j2dviewer.J2DGraphRenderer
import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.spriteManager._

import org.graphstream.ScalaGS._

object TestFillImage {
	def main( args:Array[String] ) { (new TestFillImage).run( args ) }
}

private class TestFillImage extends ViewerListener {
	private[this] var loop = true
  
	def run( args:Array[String] ) = {
		val graph  = new MultiGraph( "g1" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD )
		val pipeIn = viewer.newViewerPipe
		val view   = viewer.addView( "view1", new J2DGraphRenderer )
  
		pipeIn.addAttributeSink( graph )
		pipeIn.addViewerListener( this )
		pipeIn.pump

		graph.addAttribute( "ui.stylesheet", styleSheet )
		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.quality" )
/*
		val A = graph.addNode( "A" )
		val B = graph.addNode( "B" )

		graph.addEdge( "AB1", "A", "B" )
		graph.addEdge( "AB2", "A", "B" )
		graph.addEdge( "AB3", "A", "B" )

		A("xyz") = ( -1, 0, 0 )
		B("xyz") = (  1, 0, 0 )

		A("label") = "A"
		B("label") = "B"
*/
		val A:Node = graph.addNode( "A" )
		val B:Node = graph.addNode( "B" )
		val C:Node = graph.addNode( "C" )
		val D:Node = graph.addNode( "D" )
		val E:Node = graph.addNode( "E" )
		val F:Node = graph.addNode( "F" )

		graph.addEdges( "A", "B", "C", "A" )
		graph.addEdges( "D", "E", "A", "D" )
		graph.addEdge( "BD", "B", "D", true )
  
		graph.addEdge( "CC", "C", "C" )
		graph.addEdge( "CC2", "C", "C", true )
		graph.addEdge( "FF", "F", "F" )
		graph.addEdge( "DD", "D", "D", true )
		graph.addEdge( "BC2", "B", "C" )
		graph.addEdge( "AB2", "A", "B", true )
		graph.addEdge( "AB3", "A", "B", true )
		graph.addEdge( "AF0", "A", "F" )
		graph.addEdge( "AF1", "A", "F" )
		graph.addEdge( "AF2", "A", "F" )
		graph.addEdge( "AF3", "A", "F" )

		A("xyz") = (  0,   0,   0 )
		B("xyz") = ( -0.2, 1,   0 )
		C("xyz") = (  0.7, 0.5, 0 )
		D("xyz") = ( -1,  -1,   0 )
		E("xyz") = (  1,  -1,   0 )
		F("xyz") = (  1,   0,   0 )
  
		A("label") = "A"
		B("label") = "B"
		C("label") = "C"
		D("label") = "D"
		E("label") = "E"
 
		val sman = new SpriteManager( graph )
  
		val s1 = sman.addSprite( "S1" )
		val s2 = sman.addSprite( "S2" )
		val quit = sman.addSprite( "quit" )
  
		s1.attachToNode( "A" )
		s2.attachToEdge( "BC" )
  
		s1.setPosition( StyleConstants.Units.GU, 0.2f, 45f, 45f )
		s2.setPosition( 0f )
		quit.setPosition( StyleConstants.Units.PX, 40, 15, 0 )
		quit.addAttribute( "label", "quit" )

		var p   = 0f
		var dir = 0.005f
		var a   = 0f
		var ang = 0.01f
  
		while( loop ) {
//			p += dir
//			a += ang
//   
//			if( p > 1f ) { dir = - dir; p = 1f; s2.attachToEdge( "BC2" ) }
//			if( p < 0f ) { dir = - dir; p = 0f; s2.attachToEdge( "BC" ) }
//			if( a > 360 ) { a = 0f; }
//   
//			s1.setPosition( StyleConstants.Units.GU, 0.2f, 0f, a )
//			s2.setPosition( p )
		  
			pipeIn.pump
			sleep( 10 )
		}
		
		printf( "bye bye" )
		exit
	}
 
	protected def sleep( ms:Long ) { Thread.sleep( ms ) }

// Viewer Listener Interface
 
	def viewClosed( id:String ) { loop = false }
 
 	def buttonPushed( id:String ) {
 		if( id == "quit" )
 			loop = false
 		else if( id == "A" )
 			print( "Button A pushed%n".format() )
 	}
  
 	def buttonReleased( id:String ) {} 
 
// Data
   
	private val styleSheet:String = """
			graph {
				canvas-color: white;
 				fill-mode: image-tiled;
 				fill-image: url('data/paper.png');
 				fill-color: white, gray;
 				padding: 60px;
 			} 
			node {
				shape: box;
				size: 60px, 60px;
				fill-mode: image-scaled-ratio-max;
				fill-image: url('data/FabricPlain0081.jpg');
				fill-color: white, rgb(200,200,200);
				stroke-mode: plain; 
				stroke-color: rgba(100,100,100,255);
				stroke-width: 9px;
				shadow-mode: plain;
				shadow-width: 0px;
				shadow-offset: 3px, -3px;
				shadow-color: rgba(0,0,0,100);
				icon-mode: at-left;
				icon: url('file:///home/antoine/Documents/Perso/Art/Icons/GSLogo11d24.png');
			}
			node#E {
				size: 90px, 90px;
			}
			node:clicked {
				stroke-mode: plain;
				stroke-color: red;
			}
			node:selected {
				stroke-mode: plain;
				stroke-width: 6px;
				stroke-color: blue;
			}
			node#A {
				stroke-mode: plain;
				stroke-width: 9px;
				stroke-color: yellow;
				size: 80px, 30px;
				shape: jcomponent;
				jcomponent: button;
			}
			node#F {
				size: 20px, 20px;
				icon-mode: none;
			}
			edge {
				shape: cubic-curve;
				size: 9px;
				//fill-color: rgb(128,128,128);
				fill-color: rgba(100,100,100,255);
				fill-mode: plain;
				//stroke-mode: plain;
				//stroke-color: rgb(80,80,80);
				//stroke-width: 2px;
				shadow-mode: plain;
				shadow-color: rgba(0,0,0,50);
				shadow-offset: 3px, -3px;
				shadow-width: 0px;
				arrow-shape: arrow;
				arrow-size: 20px, 6px;
			}
			sprite {
				size: 8px;
				shape: circle;
				fill-mode: gradient-radial;
				fill-color: red, white;
				stroke-mode: plain;
				stroke-color: rgb(100,100,100);
				stroke-width: 1px;
			}
			sprite#quit {
				shape: jcomponent;
				jcomponent: button;
				size: 80px, 30px;
			}
		""";
}