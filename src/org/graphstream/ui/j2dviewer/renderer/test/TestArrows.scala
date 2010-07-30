package org.graphstream.ui.j2dviewer.renderer.test

import org.graphstream.graph.{Graph, Edge}
import org.graphstream.scalags.graph.MultiGraph

import org.graphstream.algorithm.Toolkit._

import org.graphstream.ui.graphicGraph.stylesheet.{Values, StyleConstants}
import org.graphstream.ui.swingViewer.{Viewer, DefaultView, ViewerPipe, ViewerListener}
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants
import org.graphstream.ui.spriteManager._
import org.graphstream.ui.j2dviewer._

import org.graphstream.ScalaGS._

object TestArrows {
	def main( args:Array[String] ) { (new TestArrows).run } 
}

class TestArrows extends ViewerListener {
	var loop = true
	
	def run() = {
		val graph  = new MultiGraph( "TestSize" )
		val viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD )
		val pipeIn = viewer.newViewerPipe
		val view   = viewer.addView( "view1", new J2DGraphRenderer )
//		val view   = viewer.addDefaultView( true )
  
		pipeIn.addAttributeSink( graph )
		pipeIn.addViewerListener( this )
		pipeIn.pump

		graph.addAttribute( "ui.stylesheet", styleSheet )
		graph.addAttribute( "ui.antialias" )
		graph.addAttribute( "ui.quality" )
		
		val A = graph.addNode( "A" )
		val B = graph.addNode( "B" )
		val C = graph.addNode( "C" )
		val D = graph.addNode( "D" )
		val E = graph.addNode( "E" )

		val AB = graph.addEdge( "AB", "A", "B", true )
		val BC = graph.addEdge( "BC", "B", "C", true )
		val CD = graph.addEdge( "CD", "C", "D", true )
		val DA = graph.addEdge( "DA", "D", "A", true )
		val BB = graph.addEdge( "BB", "B", "B", true )
		val DE = graph.addEdge( "DE", "D", "E", true )
		
		A("xyz") = ( 0, 1, 0 )
		B("xyz") = ( 1, 0.8, 0 )
		C("xyz") = ( 0.8, 0, 0 )
		D("xyz") = ( 0, 0, 0 )
		E("xyz") = ( 0.5, 0.5, 0 )
		
		A("label") = "A"
		B("label") = "Long label ..."
		C("label") = "C"
		D("label") = "A long label ..."
		E("label") = "Another long label"
		
		var size = 20f
		var sizeInc = 1f
			
		while( loop ) {
			pipeIn.pump
			sleep( 40 )
			A.setAttribute( "ui.size", size.asInstanceOf[AnyRef] )
			
			size += sizeInc
			
			if( size > 50 ) {
				sizeInc = -1f; size = 50f
			} else if( size < 20 ) {
				sizeInc = 1f; size = 20f
			}
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
 	
	val styleSheet = """
			graph {
				canvas-color: white;
 				fill-mode: gradient-radial;
 				fill-color: white, #EEEEEE;
 				padding: 60px;
 			} 
			node {
				shape: circle;
				size: 30px;
				fill-mode: plain;
				fill-color: #CCCC;
				stroke-mode: plain; 
				stroke-color: black;
				stroke-width: 1px;
			}
			node:clicked {
				stroke-mode: plain;
				stroke-color: red;
			}
			node:selected {
				stroke-mode: plain;
				stroke-color: blue;
			}
			node#A {
				size-mode: dyn-size;
				size: 10px;
			}
			node#B {
				shape: circle;
				size-mode: fit;
				size: 50px;
				padding: 10px;
			}
			node#C {
				shape: box;
				size: 50px;
			}
			node#D {
				shape: box;
				size-mode: fit;
				padding: 5px;
			}
			node#E {
				shape: circle;
				size-mode: fit;
				size: 20px, 10px;
				padding: 6px;
			}
			edge {
				shape: line;
				size: 1px;
				fill-color: grey;
				fill-mode: plain;
				arrow-shape: arrow;
				arrow-size: 20px, 5px;
			}
			edge#BC {
				shape: cubic-curve;
			}
			edge#AB {
				shape: cubic-curve;
			}
			"""
}