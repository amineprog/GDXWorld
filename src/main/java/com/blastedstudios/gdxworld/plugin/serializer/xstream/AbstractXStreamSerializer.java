package com.blastedstudios.gdxworld.plugin.serializer.xstream;

import java.io.FileFilter;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.blastedstudios.gdxworld.util.ExtensionFileFilter;
import com.blastedstudios.gdxworld.util.Log;
import com.blastedstudios.gdxworld.util.Properties;
import com.thoughtworks.xstream.XStream;

public abstract class AbstractXStreamSerializer {
	protected final XStream xStream;
	protected final String extension, name;
	
	public AbstractXStreamSerializer(XStream xStream, String extension, String name){
		this.xStream = xStream;
		this.extension = extension;
		this.name = name;
		xStream.alias("Vector2", com.badlogic.gdx.math.Vector2.class);
		xStream.aliasPackage("world", "com.blastedstudios.gdxworld.world");
		xStream.aliasPackage("plugin", "com.blastedstudios.gdxworld.plugin");
		if(Properties.getBool("levelconverter.marshal.use", false))
			xStream.registerConverter(new LevelConverter(xStream));
	}
	
	public Object load(InputStream stream) throws Exception {
		return xStream.fromXML(stream);
	}
	
	public Object load(FileHandle selectedFile) throws Exception {
		if(selectedFile == null){
			Log.error(this.getClass().getSimpleName() + ".load", "Cannot read null file");
			throw new NullPointerException("selectedFile null");
		}
		Object object = load(selectedFile.read());
		Log.log(this.getClass().getSimpleName() + ".load", "Successfully loaded " + selectedFile);
		return object;
	}
	
	public void save(OutputStream stream, Object object) throws Exception {
		xStream.toXML(object, stream);
	}

	public void save(FileHandle selectedFile, Object object) throws Exception {
		if(selectedFile == null){
			Log.error(this.getClass().getSimpleName() + ".save", "Cannot write to null file");
			return;
		}
		save(Gdx.files.absolute(selectedFile.file().getAbsolutePath()).write(false), object);
		Log.log(this.getClass().getSimpleName() + ".save", "Successfully saved " + selectedFile);
	}

	public FileFilter getFileFilter() {
		return new ExtensionFileFilter(extension, name);
	}
}
