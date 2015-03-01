package com.application.moveon.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.BitSet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ImageHelper {
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels,
			int rotation) {

		Bitmap tmpBitmap = getResizedBitmap(bitmap, 150, 150);

		if (rotation != 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);

			tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0,
					tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix, false);
		}

		Bitmap output = Bitmap.createBitmap(tmpBitmap.getWidth(),
				tmpBitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, tmpBitmap.getWidth(),
				tmpBitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(tmpBitmap, rect, rect, paint);

		// Soutput = Bitmap.createBitmap(final_dimension, final_dimension,
		tmpBitmap.recycle();
		tmpBitmap = null;
		
		// Config.ARGB_8888);
		return output;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();
		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();

		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	public static File registerBitmapInCache(Activity a, Bitmap b, String name) {
		File cacheDir = a.getBaseContext().getCacheDir();
		File f = new File(cacheDir, name);

		try {
			FileOutputStream out = new FileOutputStream(f);
			b.compress(Bitmap.CompressFormat.PNG, 100, out);
			b = ImageHelper.rotatePicture(f.getAbsolutePath(), b);
			//b.recycle();
			//b = null;
			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return f;
	}
	
	public static File registerFileInCache(Activity a, byte[] b, String name) {
		
		File cacheDir = a.getBaseContext().getCacheDir();
		File f = new File(cacheDir, name);

		try {
			FileOutputStream out = new FileOutputStream(f);
			out.write(b);
			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ToolBox tools = new ToolBox(a);
		byte[] b2 = new byte[0];
		try {
			b2 = tools.decodeFileFromResource(f.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return f;
	}

	public static Bitmap getBitmapFromCache(Activity a, String name) {
		
		Bitmap bitmap = null;
		FileInputStream fis = null;
		File f = null;
		
		try{
		
		File cacheDir = a.getBaseContext().getCacheDir();
		f = new File(cacheDir, name);
	    fis = new FileInputStream(f);
	    
	    BitmapFactory.Options opts=new BitmapFactory.Options();
	    /*opts.inDither=false;                     //Disable Dithering mode
	    opts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
	    opts.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
	    opts.inTempStorage=new byte[32 * 1024]; */

		bitmap = BitmapFactory.decodeStream(fis, null, opts);
		} catch (OutOfMemoryError e){
			Log.i("ANTHO", "Erreur out of memory "+ fis);
		} catch (FileNotFoundException e) {
			Log.i("ANTHO", "fis"+fis +" " + f);
			e.printStackTrace();
		}
		return bitmap;
	}

	private Bitmap decodeFile(File f){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=70;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}

	public static byte[] bitmapToByteArrayWithoutCompression(Bitmap b){

		//calculate how many bytes our image consists of.
		int bytes = b.getByteCount();
		//or we can calculate bytes this way. Use a different value than 4 if you don't use 32bit images.
		//int bytes = b.getWidth()*b.getHeight()*4; 

		ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
		b.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

		return buffer.array(); //Get the underlying array containing the data.
	}
	
	public static byte[] bitmapToByteArrayWithCompression(Bitmap b) throws IOException{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.reset();
		b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] result = stream.toByteArray();
		stream.flush();
		stream.close();
		return result;
	}
	
	public static Bitmap rotatePicture(String path, Bitmap bitmap) throws IOException{
		File imageFile = new File(path);
	      ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
	      int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	      int rotate = 0;
	      
	      switch (orientation) {
	      case ExifInterface.ORIENTATION_ROTATE_270:
	        rotate = 270;
	        break;
	      case ExifInterface.ORIENTATION_ROTATE_180:
	        rotate = 180;
	        break;
	      case ExifInterface.ORIENTATION_ROTATE_90:
	        rotate = 90;
	        break;
	      }
	      
        Bitmap rotatedBitmap = rotate(bitmap, rotate);
        return rotatedBitmap;
	}
	
	public static Bitmap rotate(Bitmap b, int degrees) {
	    if (degrees != 0 && b != null) {
	      Matrix m = new Matrix();

	      m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
	      try {
	        Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
	        if (b != b2) {
	          b.recycle();
	          b = b2;
	        }
	      } catch (OutOfMemoryError ex) {
	        throw ex;
	      }
	    }
	    return b;
	  }
	
	public static final String insertImage(String id, 
			ContentResolver cr, 
			Bitmap source, 
			String title, 
			String description) {
		
		ContentValues values = new ContentValues();
		//values.put(Images.Media.TITLE, title);
		//values.put(Images.Media.DISPLAY_NAME, title);
		values.put(Images.Media.DESCRIPTION, id);
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		// Add the date meta data to ensure the image is added at the front of the gallery
		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
		values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
 
        Uri url = null;
        String stringUrl = null;    /* value to be returned */
 
        try {
            url = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
 
            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                } finally {
                    imageOut.close();
                }
 
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }
 
        if (url != null) {
            stringUrl = url.toString();
        }
 
        return stringUrl;
	}

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    static final int MAX_IMAGE_DIMENSION = 120;
    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }
}