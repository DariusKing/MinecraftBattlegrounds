package net.abstractiondev.mcbg.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;

import net.abstractiondev.mcbg.BattlegroundsPlugin;
import net.abstractiondev.mcbg.data.Arena;

public class DataLoader
{	
	private BattlegroundsPlugin plugin;
	public DataLoader(BattlegroundsPlugin plugin)
	{
		this.plugin = plugin;
		
		key = getRemoteEncryptionKey().getBytes();
		plugin.log.info("Encryption key is '" + new String(key) + "'.");
	}
	
	private Arena loadArena(File input) throws ArenaLoadException
	{ // Decrypt then deserialize
		try {
			try {
				return ((Arena) decrypt(new FileInputStream(input)));
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ArenaLoadException(e);
		}
		return null;
	}
	
	private boolean saveArena(Arena arena, File output) throws ArenaSaveException
	{ // Serialize then encrypt
		try
		{
			try {
				encrypt(arena,new FileOutputStream(output));
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
		catch(IOException e)
		{
			return false;
		}
	}

	public void saveArenas()
	{
		File f;
		for(Arena arena : plugin.arenas)
		{
			f = new File(plugin.getDataFolder() + File.separator + "arenas" + File.separator + arena.identifier.toLowerCase() + ".bga");
			try {
				plugin.log.info("Saving arena to file '" + f.getAbsolutePath() + "'.");
				saveArena(arena, f);
			} catch (ArenaSaveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public boolean loadArenas()
	{
		plugin.log.info("Loading Battlegrounds Arenas...");
		
		Path path = Paths.get(plugin.getDataFolder().getAbsolutePath(),"arenas");
		if(path.toFile().exists())
		{
			ArrayList<File> files = getArenaFiles((new File(plugin.getDataFolder() + File.separator + "arenas")).listFiles());
			
			if(files.size() > 0)
			{
				Arena arena;
				for(File f : files)
				{
					if(f.isFile())
					{
						plugin.log.info("Loading arena from file '" + f.getAbsolutePath() + "'.");
						
						try {
							arena = loadArena(f);
							
							if(arena != null)
							{
								plugin.arenas.add(arena);
								plugin.log.info("Loaded arena from file '" + f.getAbsolutePath() + "'.");
							}
							else
							{
								plugin.log.info("Failed to load arena from file '" + f.getAbsolutePath() + "'.");
							}
						} catch (ArenaLoadException e) {
							// TODO Auto-generated catch block
							plugin.log.info("Failed to load arena from file '" + f.getAbsolutePath() + "'.");
						}
					}
					
					plugin.log.info(f.getAbsolutePath());
				}

				plugin.log.info("Loaded " + files.size() + " arenas from file.");
				
				return true;
			}
			else
			{
				plugin.log.info("No dynamic arenas found in directory.");
				return true;
			}
		}
		else
		{
			plugin.log.info("Arenas directory was not found in installation path.");
			return false;
		}
	}
	
	private ArrayList<File> getArenaFiles(File[] listFiles) {
		ArrayList<File> list = toArrayList(listFiles);
		ArrayList<File> nlist = new ArrayList<File>();
		
		int i = 0;
		for(File f : listFiles)
		{
			try
			{ // Check if the file is a BGA File (Battlegrounds Arena)
				if(f.getName().substring(f.getName().lastIndexOf('.')).equalsIgnoreCase(".bga"))
					nlist.add(list.get(i));
			}
			catch(StringIndexOutOfBoundsException e)
			{
				continue;
			}
			
			i++;
		}
		
		return nlist;
	}
	
	private ArrayList<File> toArrayList(File[] array)
	{
		ArrayList<File> list = new ArrayList<File>();
		for(File elem : array)
		{
			list.add(elem);
		}
		return list;
	}
	
	// Encryption Key (TODO: Retrieve from upstream) -> 14e31951c41ad0aac6706783eb49e415678e7270332d67ed3921eaadcd44cf35
	private static byte[] key = null;
	private static String transformation = "AES";

	public static void encrypt(Serializable object, OutputStream ostream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{		
	    try {
	        // Length is 16 byte
	        SecretKeySpec sks = new SecretKeySpec(key, transformation);

	        // Create cipher
	        Cipher cipher = Cipher.getInstance(transformation);
	        cipher.init(Cipher.ENCRYPT_MODE, sks);
	        SealedObject sealedObject = new SealedObject(object, cipher);

	        // Wrap the output stream
	        CipherOutputStream cos = new CipherOutputStream(ostream, cipher);
	        ObjectOutputStream outputStream = new ObjectOutputStream(cos);
	        outputStream.writeObject(sealedObject);
	        outputStream.close();
	    } catch (IllegalBlockSizeException e) {
	        e.printStackTrace();
	    }
	}

	private String getRemoteEncryptionKey()
	{
		plugin.log.info("Downloading encryption key from remote server 'abstractiondev.net'...");
		
		try {
			URL url = new URL("http://assets.abstractiondev.net/mcbg/key.php");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			String s;
			while((s = reader.readLine()) != null)
			{
				return s;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "INVALID_ENCR_KEY";
		
	}

	public static Object decrypt(InputStream istream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
	    SecretKeySpec sks = new SecretKeySpec(key, transformation);
	    Cipher cipher = Cipher.getInstance(transformation);
	    cipher.init(Cipher.DECRYPT_MODE, sks);

	    CipherInputStream cipherInputStream = new CipherInputStream(istream, cipher);
	    ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
	    SealedObject sealedObject;
	    try
	    {
	        sealedObject = (SealedObject) inputStream.readObject();
	        inputStream.close();
	        return sealedObject.getObject(cipher);
	    }
	    catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			inputStream.close();
			return null;
		}
	}

	class ArenaLoadException extends IOException
	{
		private static final long serialVersionUID = 634096980659398156L;

		ArenaLoadException(Exception e)
		{
			super(e.getMessage());
		}
	}
	class ArenaSaveException extends IOException
	{
		private static final long serialVersionUID = -5846865168953630094L;

		ArenaSaveException(Exception e)
		{
			super(e.getMessage());
		}
	}
}

