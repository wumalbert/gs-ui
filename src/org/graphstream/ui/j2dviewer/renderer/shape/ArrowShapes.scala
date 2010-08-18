package org.graphstream.ui.j2dviewer.renderer.shape

import java.awt._
import java.awt.geom._
import java.awt.image._

import scala.math._

import org.graphstream.ui.geom.Point2
import org.graphstream.ui.sgeom._
import org.graphstream.ui.graphicGraph._
import org.graphstream.ui.graphicGraph.stylesheet._
import org.graphstream.ui.j2dviewer._
import org.graphstream.ui.util._
import org.graphstream.ui.j2dviewer.renderer._
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants._

class ArrowOnEdge extends AreaOnConnectorShape {
	val theShape = new Path2D.Float()
 
// Command
 
	protected def make( g:Graphics2D, camera:Camera ) { make( false, camera ) }
	protected def makeShadow( g:Graphics2D, camera:Camera ) { make( true, camera ) }
  
	protected def make( forShadow:Boolean, camera:Camera ) {
		if( theConnector.info.isCurve )
		     makeOnCurve( forShadow, camera )
		else makeOnLine(  forShadow, camera )
	}
 
	protected def makeOnLine( forShadow:Boolean, camera:Camera ) {
		var off = ShapeUtil.evalTargetRadius( theEdge, camera )
		val theDirection = new Vector2(
			theConnector.toPos.x - theConnector.fromPos.x,
			theConnector.toPos.y - theConnector.fromPos.y )
			
		theDirection.normalize
  
		var x    = theCenter.x - ( theDirection(0) * off )
		var y    = theCenter.y - ( theDirection(1) * off )
		val perp = new Vector2( theDirection(1), -theDirection(0) )
		
		perp.normalize
		theDirection.scalarMult( theSize.x )
		perp.scalarMult( theSize.y )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
  
		// Create a polygon.
		
		theShape.reset
		theShape.moveTo( x , y )
		theShape.lineTo( x - theDirection(0) + perp(0), y - theDirection(1) + perp(1) )		
		theShape.lineTo( x - theDirection(0) - perp(0), y - theDirection(1) - perp(1) )
		theShape.closePath
	}
	
	protected def makeOnCurve( forShadow:Boolean, camera:Camera ) {
		val (p1,t) = CubicCurve.approxIntersectionPointOnCurve( theEdge, theConnector, camera )
		val style  = theEdge.getStyle
		
		val p2 = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t-0.05f )
		var dir = Vector2( p1.x - p2.x, p1.y - p2.y )			// XXX The choice of the number above (0.05f) is problematic
		dir.normalize											// Clearly it should be chosen according to the length
		dir.scalarMult( theSize.x )								// of the arrow compared to the length of the curve, however
		var per = Vector2( dir(1), -dir(0) )					// computing the curve length (see CubicCurve) is costly. XXX
		per.normalize
		per.scalarMult( theSize.y )
		
		// Create a polygon.

		theShape.reset
		theShape.moveTo( p1.x , p1.y )
		theShape.lineTo( p1.x - dir(0) + per(0), p1.y - dir(1) + per(1) )		
		theShape.lineTo( p1.x - dir(0) - per(0), p1.y - dir(1) - per(1) )
		theShape.closePath		
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( true, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( false, camera )
 		stroke( g, theShape )
 		fill( g, theShape, camera )
	}
}

class CircleOnEdge extends AreaOnConnectorShape {
	val theShape = new Ellipse2D.Float
 
// Command
 
	protected def make( g:Graphics2D, camera:Camera )       { make( false, camera ) }
	protected def makeShadow( g:Graphics2D, camera:Camera ) { make( true, camera ) }
  
	protected def make( forShadow:Boolean, camera:Camera ) {
		if( theConnector.info.isCurve )
		     makeOnCurve( forShadow, camera )
		else makeOnLine(  forShadow, camera )
	}
 
	protected def makeOnLine( forShadow:Boolean, camera:Camera ) {
		val off = ShapeUtil.evalTargetRadius( theEdge, camera ) + ((theSize.x+theSize.y)/4)
		val theDirection = new Vector2(
			theConnector.toPos.x - theConnector.fromPos.x,
			theConnector.toPos.y - theConnector.fromPos.y )
			
		theDirection.normalize
  
		var x    = theCenter.x - ( theDirection(0) * off )
		var y    = theCenter.y - ( theDirection(1) * off )
		//val perp = new Vector2( theDirection(1), -theDirection(0) )
		
		//perp.normalize
		theDirection.scalarMult( theSize.x )
		//perp.scalarMult( theSize.y )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
  
		// Set the shape.
		
		theShape.setFrame( x-(theSize.x/2), y-(theSize.y/2), theSize.x, theSize.y )
	}

	protected def makeOnCurve( forShadow:Boolean, camera:Camera ) {
		val (p1,t) = CubicCurve.approxIntersectionPointOnCurve( theEdge, theConnector, camera )
		val style  = theEdge.getStyle
		
		val p2 = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t-0.1f )
		var dir = Vector2( p1.x - p2.x, p1.y - p2.y )
		dir.normalize
		dir.scalarMult( theSize.x/2 )

		// Create a polygon.

		theShape.setFrame( (p1.x-dir.x)-(theSize.x/2), (p1.y-dir.y)-(theSize.y/2), theSize.x, theSize.y )
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( true, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( false, camera )
 		stroke( g, theShape )
 		fill( g, theShape, camera )
	}
 
	protected def lengthOfCurve( c:Connector ):Float = {
		// Computing a curve real length is really heavy.
		// We approximate it using the length of the 3 line segments of the enclosing
		// control points.
		( c.fromPos.distance( c.byPos1 ) + c.byPos1.distance( c.byPos2 ) + c.byPos2.distance( c.toPos ) ) * 0.75f
	}
}

class DiamondOnEdge extends AreaOnConnectorShape {
	val theShape = new Path2D.Float()
 
// Command
 
	protected def make( g:Graphics2D, camera:Camera ) { make( false, camera ) }
	protected def makeShadow( g:Graphics2D, camera:Camera ) { make( true, camera ) }
  
	protected def make( forShadow:Boolean, camera:Camera ) {
		if( theConnector.info.isCurve )
		     makeOnCurve( forShadow, camera )
		else makeOnLine(  forShadow, camera )
	}
 
	protected def makeOnLine( forShadow:Boolean, camera:Camera ) {
		var off = ShapeUtil.evalTargetRadius( theEdge, camera )
		val theDirection = new Vector2(
			theConnector.toPos.x - theConnector.fromPos.x,
			theConnector.toPos.y - theConnector.fromPos.y )
			
		theDirection.normalize
  
		var x    = theCenter.x - ( theDirection(0) * off )
		var y    = theCenter.y - ( theDirection(1) * off )
		val perp = new Vector2( theDirection(1), -theDirection(0) )
		
		perp.normalize
		theDirection.scalarMult( theSize.x / 2 )
		perp.scalarMult( theSize.y )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
  
		// Create a polygon.
		
		theShape.reset
		theShape.moveTo( x , y )
		theShape.lineTo( x - theDirection(0) + perp(0), y - theDirection(1) + perp(1) )	
		theShape.lineTo( x - theDirection(0)*2, y - theDirection(1)*2 )
		theShape.lineTo( x - theDirection(0) - perp(0), y - theDirection(1) - perp(1) )
		theShape.closePath
	}
	
	protected def makeOnCurve( forShadow:Boolean, camera:Camera ) {
		val (p1,t) = CubicCurve.approxIntersectionPointOnCurve( theEdge, theConnector, camera )
		val style  = theEdge.getStyle
		
		val p2  = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t-0.1f )
		var dir = Vector2( p1.x - p2.x, p1.y - p2.y )
		dir.normalize
		dir.scalarMult( theSize.x )
		var per = Vector2( dir(1), -dir(0) )
		per.normalize
		per.scalarMult( theSize.y )
		
		// Create a polygon.

		theShape.reset
		theShape.moveTo( p1.x , p1.y )
		theShape.lineTo( p1.x - dir(0)/2 + per(0), p1.y - dir(1)/2 + per(1) )
		theShape.lineTo( p1.x - dir(0), p1.y - dir(1) )
		theShape.lineTo( p1.x - dir(0)/2 - per(0), p1.y - dir(1)/2 - per(1) )
		theShape.closePath
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( true, camera )
 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( false, camera )
 		stroke( g, theShape )
 		fill( g, theShape, camera )
	}
}


/** Put an image as the arrow of the edge. */
class ImageOnEdge extends AreaOnConnectorShape {
 
// Command
 
	var image:BufferedImage = null
	var p:Point2D.Float = null
	var angle = 0f
	
	override def configureForGroup( g:Graphics2D, style:Style, camera:Camera ) {
		super.configureForGroup( g, style, camera )
	}
	
	override def configureForElement( g:Graphics2D, element:GraphicElement, info:ElementInfo, camera:Camera ) {
		super.configureForElement( g, element, info, camera )
		
		var url = element.getStyle.getArrowImage
		
		if( url.equals( "dynamic" ) ) {
			if( element.hasLabel( "ui.arrow-image" ) )
			      url = element.getLabel( "ui.arrow-image" ).toString
			else url = null
		}
			
		if( url != null ) {
			image = ImageCache.loadImage( url ) match {
				case x:Some[_] => x.get
				case _         => ImageCache.dummyImage
			}
		}
	}
	
	protected def make( g:Graphics2D, camera:Camera ) { make( false, camera ) }
	protected def makeShadow( g:Graphics2D, camera:Camera ) { make( true, camera ) }
  
	protected def make( forShadow:Boolean, camera:Camera ) {
		if( theConnector.info.isCurve )
		     makeOnCurve( forShadow, camera )
		else makeOnLine(  forShadow, camera )
	}
 
	protected def makeOnLine( forShadow:Boolean, camera:Camera ) {
		var off = ShapeUtil.evalTargetRadius( theEdge, camera )
		val theDirection = new Vector2(
			theConnector.toPos.x - theConnector.fromPos.x,
			theConnector.toPos.y - theConnector.fromPos.y )
			
		theDirection.normalize
		
		val iw = camera.metrics.lengthToGu( image.getWidth, Units.PX ) / 2
		var x  = theCenter.x - ( theDirection(0) * ( off + iw ) )
		var y  = theCenter.y - ( theDirection(1) * ( off + iw ) )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
		
		p     = camera.transform( x, y )	// Pass to pixels, the image will be drawn in pixels.
		angle = acos( theDirection.dotProduct( 1, 0 ) ).toFloat
		
		if( theDirection.y > 0 )			// The angle is always computed for acute angles
			angle = ( Pi - angle ).toFloat
	}
	
	protected def makeOnCurve( forShadow:Boolean, camera:Camera ) {
		val (p1,t) = CubicCurve.approxIntersectionPointOnCurve( theEdge, theConnector, camera )
		val style  = theEdge.getStyle
		val p2  = CubicCurve.eval( theConnector.fromPos, theConnector.byPos1, theConnector.byPos2, theConnector.toPos, t-0.1f )
		var dir = Vector2( p1.x - p2.x, p1.y - p2.y )
		
		dir.normalize
	
		val iw = camera.metrics.lengthToGu( image.getWidth, Units.PX ) / 2
		var x  = p1.x - ( dir(0) * iw )
		var y  = p1.y - ( dir(1) * iw )
		
		if( forShadow ) {
			x += theShadowOff.x
			y += theShadowOff.y
		}
		
		p     = camera.transform( x, y )
		angle = acos( dir.dotProduct( 1, 0 ) ).toFloat
		
		if( dir.y > 0 )
			angle = ( Pi - angle ).toFloat
	}
 
	def renderShadow( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
// 		make( true, camera )
// 		cast( g, theShape )
	}
 
	def render( g:Graphics2D, camera:Camera, element:GraphicElement, info:ElementInfo ) {
 		make( false, camera )
// 		stroke( g, theShape )
// 		fill( g, theShape, camera )
 		
 		if( image ne null ) {
 			val Tx = g.getTransform
 			val Tr = new AffineTransform
 			
 			g.setTransform( Tr )									// An identity matrix.
 			Tr.translate( p.x, p.y )								// 3. Position the image at its position in the graph.
 			Tr.rotate( angle )										// 2. Rotate the image from its center.
 			Tr.translate( -image.getWidth/2, -image.getHeight/2 )	// 1. Position in center of the image.
 			g.drawImage( image, Tr, null )							// Paint the image.
 			g.setTransform( Tx )									// Restore the original transform
 		}
	}
}