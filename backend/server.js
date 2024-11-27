require('dotenv').config({ path: './mongo.env' });

const express = require('express');
const mongoose = require('mongoose');
const crypto = require('crypto');
const cors = require('cors');
const bodyParser = require('body-parser');
const multer = require('multer');

const app = express();
const port = process.env.PORT || 3000;

// ALLOW REQUEST FROM 5173
const corsOptions = {
  origin: 'http://localhost:5173',
  methods: ['GET', 'POST', 'DELETE'],
};
// Middleware
app.use(cors(corsOptions));
app.use(express.json());




// Define the User model
const userSchema = new mongoose.Schema({
  username: { type: String, required: true },
  password: { type: String, required: true },
});
const User = mongoose.model('User', userSchema);

// CONNECT TO MONGODB ATLAS
const mongoURI =process.env.MONGODB_URI;
mongoose.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('Connected to MongoDB'))
  .catch(err => console.error('Error connecting to MongoDB:', err));


// SCHEMA
const songSchema = new mongoose.Schema({
  fileName: String,
  songName: String,          
  artistName: String,    
  albumName: String,     
  releaseDate: String,
  artWorkURL: String,
  mood: String, // MOOD OF THE SONG MAYBE FOR RECOMMENDATION?     
  data: Buffer            
});
const SongModel = mongoose.model('Songs', songSchema);
// const UserModel = mongoose.model('User', songSchema);

//Artist list 



// MULTER FOR FILE UPLOAD
const storage = multer.memoryStorage();
const fileFilter = function (req, file, cb) {
  // Check if uploaded file is an .mp3 file
  if (file.mimetype === 'audio/mpeg' || file.mimetype === 'audio/mp3') {
    cb(null, true); // Accept file
  } else {
    cb(new Error('Only .mp3 files are allowed!'), false); // Reject file
  }
};
const upload = multer({ storage: storage, fileFilter: fileFilter });


//LOGIN
app.post('/api/login', async (req, res) => {
  try {
    const { username, password } = req.body;
    const hashedPassword = crypto.createHash('md5').update(password).digest('hex');

    const user = await User.findOne({ username, password: hashedPassword });
    if (user) {
      res.status(200).json({ message: 'Login successful' });
    } else {
      res.status(401).json({ message: 'Invalid username or password' });
    }
  } catch (error) {
    console.error('Error during login:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
});


//SIGNUP
app.post('/api/signup', async (req, res) => {
  try {
    console.log('Received new user data:', req.body);

    const hashedPassword = crypto.createHash('md5').update(req.body.password).digest('hex');
    const newUser = new User({
    username: req.body.username,
    password: hashedPassword,
    });
    await newUser.save();
    
  
    console.log('User saved:', newUser);
    res.status(201).json(newUser);
  } catch (error) {
    console.error('Error saving contact:', error); // Log detailed error
    res.status(500).json({ message: 'Error creating contact', error: error.message });
  }
});


// POST UPLOAD
app.post('/upload', upload.single('songFile'), async (req, res) => {
    try {
      const songData = req.file.buffer;     
      const fileName = req.file.originalname;  
  
      const { songName, artistName, albumName, releaseDate, artWorkURL, mood } = req.body;
  
      const song = new SongModel({
        fileName: fileName,
        songName: songName,
        artistName: artistName,
        albumName: albumName,
        releaseDate: releaseDate,
        artWorkURL: artWorkURL,
        mood: mood,
        data: songData
      });
      await song.save();
  
      res.status(201).send('MP3 file uploaded successfully');
    } catch (err) {
      console.error(err);
      res.status(500).send('Error uploading MP3 file');
    }
  });




// GET TO PLAY BY songName
app.get('/play/:songName', async (req, res) => {
    const songName = req.params.songName; 
    try {
      const songFile = await SongModel.findOne({ songName: songName });
      
      if (!songFile) {
        return res.status(404).json({ error: 'file not found' });
      }
  
      res.set('Content-Type', 'audio/mp3');
  
      res.send(songFile.data);
    } catch (err) {
      console.error(err);
      res.status(500).json({ error: 'Internal server error' });
    }
  });


// GET THE INFORMATION OF THE SONG JSON DATA
app.get('/get/:name', async (req, res) => {
  const songName = req.params.name;
  console.log(songName);
  try {
    // never include data
    const song = await SongModel.findOne({ songName: songName }, '-data');

    if (!song) {
      return res.status(404).json({ error: 'File not found' });
    }

    res.json(song);
  } catch (err) {
    console.error('Error fetching song:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
});




  
// GET THE INFORMATION OF ALL SONGS
app.get('/api/all', async (req, res) => {
  console.log('Request received at /api/all');
  try {
      // const songs = await SongModel.find({});
      const songs = await SongModel.find({}, '-data'); // Exclude the 'data' field
      console.log('Songs fetched from database:', songs);

    if (!songs.length) {
      return res.status(404).json({ error: 'No songs found' });
    }

    res.json(songs);
  } catch (err) {
    console.error('Error fetching songs:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
});


// RANDOM GET SONGS FOR DISCOVER IN HOMEPAGE

app.get('/random/all', async (req, res) => {
  console.log('Request received at /random/all');
  try {
    const songs = await SongModel.aggregate([
      { $sample: { size: 2 } }, // Randomly select 4 documents
      { $project: { data: 0 } } // Exclude the 'data' field from the response
    ]).allowDiskUse(true); 

    console.log('Songs fetched from database:', songs);

    if (!songs.length) {
      return res.status(404).json({ error: 'No songs found' });
    }

    res.json(songs);
  } catch (err) {
    console.error('Error fetching songs:', err);
    res.status(500).json({ error: 'Internal server error', message: err.message });
  }
});



app.get('/api/artist/:artist', async (req, res) => {
  const artist = req.params.artist;
  try {
    // Attempt to find songs by the artist
    const songsList = await SongModel.find({ artistName: artist },'-data');

    // If no songs are found, return a 404 response
    if (!songsList || songsList.length === 0) {
      return res.status(404).json({ message: 'No songs found for the specified artist' });
    }

    // Return the list of songs
    // console.log(songsList);
    res.json(songsList);
  } catch (error) {
    // Log the error with additional context
    console.error(`Error fetching songs for artist ${artist}:`, error);

    // Send a 500 response with the error details
    res.status(500).json({ message: 'Internal Server Error', error: error.message });
  }
});

// artistlist
app.get('/api/artists', async (req, res) => {
  try {
    const songs = await SongModel.find({},'-data'); // Fetch all artists (assuming SongModel represents artists)
    console.log(songs);
    res.json(songs); // Send the fetched artists as JSON response
  } catch (error) {
    console.error('Error fetching artists:', error);
    res.status(500).json({ error: 'Internal Server Error' }); // Handle error response
  }
});

// RANDOM 4 ARTISTS FOR HOME
app.get('/random/artists', async (req, res) => {
  console.log('Request received at /random/artists');
  try {
    const artists = await SongModel.aggregate([
      { $sample: { size: 2 } }, // Randomly select 4 documents
      { $project: { data: 0 } } 
    ]).allowDiskUse(true); 

    console.log('Artists fetched from database:', artists);

    if (!artists.length) {
      return res.status(404).json({ error: 'No artists found' });
    }

    res.json(artists);
  } catch (err) {
    console.error('Error fetching artists:', err);
    res.status(500).json({ error: 'Internal server error', message: err.message });
  }
});


// Searchbar
app.get('/api/search', async (req, res) => {
  const query = req.query.q;

  try {
    const songs = await SongModel.find({
      $or: [
        { songName: { $regex: query, $options: 'i' } },
        { albumName: { $regex: query, $options: 'i' } },
        { artistName: { $regex: query, $options: 'i' } },
        { artWorkURL: { $regex: query, $options: 'i' } }
      ]
    }).select('songName artWorkURL artistName albumName');

    res.json(songs);
  } catch (err) {
    console.error('Error during song search:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
});

app.listen(port, () => {
  console.log(`Server listening at http://localhost:${port}`);
});


//PLAYLIST API's BELOW
// SCHEMA
const playlistSchema = new mongoose.Schema({
  user: String, //Which users playlist this is
  songName: String, //One of the songs that should be in the playlist         
  playListNumber: Number, //Which playlist the song should go in           
});
const PlaylistsModel = mongoose.model('Playlists', playlistSchema);

app.post('/addToPlaylist', async (req, res) => {
  const { user, songName, playListNumber } = req.body;
  if (!user || !songName || playListNumber === undefined) {
    return res.status(400).send('User, songName, and playListNumber are required');
  }

  const newEntry = new PlaylistsModel({
    user: user,
    songName: songName,
    playListNumber: playListNumber,
  });

  try {
    await newEntry.save();
    res.status(201).send('Song added to playlist successfully');
  } catch (error) {
    console.error('Error adding song to playlist:', error);
    res.status(500).send('Internal Server Error');
  }
});


//GET ALL PLAYLISTS FOR A USER
app.get('/usersPlaylists/:user', async (req, res) => {
  const user = req.params.user; // Get the user from path parameters

  if (!user) {
      return res.status(400).json({ message: 'User parameter is required' });
  }

  try {
      const playlists = await PlaylistsModel.find({ user });

      if (!playlists.length) {
          return res.status(404).json({ message: 'No playlists found for this user' });
      }

      res.status(200).json(playlists);
  } catch (error) {
      console.error('Error fetching playlists:', error);
      res.status(500).json({ message: 'Internal server error' });
  }
});

// DELETE A SONG FROM A PLAYLIST
app.delete('/removeFromPlaylist', async (req, res) => {
  const { songName, playListNumber } = req.body;

  if ( !songName || playListNumber === undefined) {
    return res.status(400).send('songName, and playListNumber are required');
  }

  try {
    const result = await PlaylistsModel.findOneAndDelete({
      songName: songName,
      playListNumber: playListNumber
    });

    if (!result) {
      return res.status(404).send('Song not found in the specified playlist');
    }

    res.status(200).send('Song removed from playlist successfully');
  } catch (error) {
    console.error('Error removing song from playlist:', error);
    res.status(500).send('Internal Server Error');
  }
});
//-----------------------------------------------------------------------------------------------------------------------------



// Middleware to parse URL-encoded data (from form submissions)
app.use(express.urlencoded({ extended: true }));

app.use(express.json());
// Define the schema for the favorite song
const FavsongSchema = new mongoose.Schema({
  user: { type: String, required: true },
  songName: { type: String, required: true },
  artistName: { type: String, required: true },
  artWorkURL: { type: String, required: true, validate: /https?:\/\/.*/ },
   // Optional field with enum values
});

// Create the model from the schema
const FavoriteModel = mongoose.model('FavoriteSong', FavsongSchema);

// Add song to favorites
app.post('/api/addToFavorites', async (req, res) => {
  const { user, songName, artistName, artWorkURL, mood } = req.body;

  // Validate the request body to ensure required fields are provided
  if (!user || !songName || !artistName || !artWorkURL) {
    return res.status(400).send('User, songName, artistName, and artWorkURL are required');
  }

  try {
    // Check if the song is already in the user's favorites
    const existingFav = await FavoriteModel.findOne({ user, songName, artistName });
    if (existingFav) {
      return res.status(400).send('Song is already in your favorites');
    }

    // Create a new favorite entry
    const newFavorite = new FavoriteModel({ user, songName, artistName, artWorkURL, mood });
    await newFavorite.save();

    res.status(201).send('Song added to favorites successfully');
  } catch (error) {
    console.error('Error adding song to favorites:', error.message);
    res.status(500).send('Internal Server Error');
  }
});

// GET FAVORITE SONGS FOR A USER
app.get('/favorites/:user', async (req, res) => {
  const user = req.params.user;
  if (!user) {
    return res.status(400).json({ message: 'User parameter is required' });
  }

  try {
    const favorites = await FavoriteModel.find({ user });
    if (!favorites.length) {
      return res.status(404).json({ message: 'No favorite songs found for this user' });
    }
    res.status(200).json(favorites);
  } catch (error) {
    console.error('Error fetching favorites:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
});

// REMOVE SONG FROM FAVORITES
app.delete('/removeFromFavorites', async (req, res) => {
  const { user, songName } = req.body;
  if (!user || !songName) {
    return res.status(400).send('User and songName are required');
  }

  try {
    const result = await FavoriteModel.findOneAndDelete({ user, songName });
    if (!result) {
      return res.status(404).send('Song not found in favorites');
    }
    res.status(200).send('Song removed from favorites successfully');
  } catch (error) {
    console.error('Error removing song from favorites:', error);
    res.status(500).send('Internal Server Error');
  }
});

// GET all playlists for a user
app.get('/api/getfavPlaylists/:user', async (req, res) => {
  const user = req.params.user;  // Get the user from path parameters

  if (!user) {
    return res.status(400).json({ message: 'User parameter is required' });
  }

  try {
    // Fetch playlists for the user from the database
    const playlists = await PlaylistsModel.find({ user });

    if (!playlists.length) {
      return res.status(404).json({ message: 'No playlists found for this user' });
    }

    res.status(200).json(playlists);  // Return playlists as JSON
  } catch (error) {
    console.error('Error fetching playlists:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
});

