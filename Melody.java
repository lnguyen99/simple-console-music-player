// Linh Nguyen 2021 lnguyen@colgate.edu

public class Melody {
	private double duration; 
	private QueueInterface<Note> melody; 
	
	//Initializes the melody to store the queue of notes
	public Melody(QueueInterface<Note> song) {
		melody = song;  
		duration = calculateDuration(); 
	}
	
	//Returns the total length of the song in seconds. 
	public double getTotalDuration() {
		return duration;
	} 
	
	//Calculate the total length of the song in seconds
	public double calculateDuration() { 
		double time = 0.0; 
		int count = 0; 
		boolean startRepeat = false; 
		
		while (count < melody.size()) { 
			Note n = melody.dequeue(); 
			time += n.getDuration(); 
			
			//If the song includes a repeated section 
			//the length should include that repeated section twice. 
			if (startRepeat || n.isRepeat()) 
				time += n.getDuration();
			if (n.isRepeat()) //if find true again, end the repeat section
				startRepeat = !startRepeat; 
			
			melody.enqueue(n); 
			count ++; 
		}
		
		return time; 
	}
	
	//Returns a string of all the notes
	//Each note in its own lines
	public String toString() {
		StringBuilder s = new StringBuilder(); 
		int count = 0; 
		
		while (count < melody.size()) { 
			Note n = melody.dequeue(); 
			s.append(n + "\n");
			melody.enqueue(n); 
			count ++; 
		}
		
		return s.toString(); 
	}	
	
	//Changes the tempo of each note to be tempo percent of what it formerly was
	public void changeTempo(double tempo) { 
		int count = 0; 
		
		while (count < melody.size()) { 
			Note n = melody.dequeue(); 
			n.setDuration(n.getDuration() * tempo);
			melody.enqueue(n); 
			count ++; 
		} 
		//Re-calculate the total duration after the tempo change 
		duration *= tempo; 
	} 
	
	
/* Reverses the order of notes in the song 
** Future calls to the play methods will play the notes 
** in the opposite of the order they were in before reverse was called. 
** pre:  A, F, G, B 
** post: B, G, F, A
** Can use one temporary stack to help
*/
	public void reverse() { 
		StackInterface<Note> backward = new VectorStack<Note>(); 
		
		while (!melody.isEmpty()) { 
			backward.push(melody.dequeue()); 
		} 
		
		while (!backward.isEmpty()) {
			melody.enqueue(backward.pop()); 
		}
	}
	
	
/* Adds all notes from the other song to the end of this song
** Pre: this song is A, F, G, B and the other is F, C, D
** PostL: this song is A, F, G, B, F, C, D and the other is F, C, D
*/
	public void append(Melody other) { 
		int count = 0; 
		
		while (count < other.melody.size()) { 
			//add this song
			this.melody.enqueue(other.melody.getFront()); 
			//keep the other song intact 
			other.melody.enqueue(other.melody.dequeue()); 
			count ++;
		}
		//Re-calculate the duration to accomodate the appended section 
		duration += other.getTotalDuration();  
	}
	
/* Play all the notes in the song without changing the song 
** Repeat section is encapsulated inside the 2 true repeat values
** After repeat section ends, play that repeat once
** Must be able to call several times with the same results
** Can use one temporary queue to help
*/ 	
	public void play() { 
		int count = 0; 
		boolean startRepeat = false; 
		QueueInterface<Note> repeat = new LinkedQueue<Note>();
		
		while (count < melody.size()) { 
			Note n = melody.dequeue(); 
			n.play(); 
			
			if (n.isRepeat()) {
				//make sure the signal is changed to false
				//only repeat once
				repeat.enqueue( new Note( n.getDuration(), n.getPitch(),
								n.getOctave(), n.getAccidental(), false ) ); 
				
				if (startRepeat) {
					//play that repeat section after the end is reached
					Melody again = new Melody(repeat); 
					again.play();
					
					repeat = new LinkedQueue<Note>();
				}
				
				startRepeat = !startRepeat; 
				
			} else if (startRepeat)		//add notes to repeat section 
				repeat.enqueue(n); 

			melody.enqueue(n); 
			count ++; 
		}
	} 
	
}		
		