# Byte Ring Buffer

A ring buffer (or circular buffer) for Java that stores bytes as efficiently as possible. Ideal for audio
recording and playback.


## Example

```java
ByteRingBuffer buffer = new ByteRingBuffer(8000);

// Adds bytes to buffer.
int added = buffer.add(new byte[] { 1, 2, 3, 4 ... });

// Like add but overwrites existing data when buffer becomes full. 
buffer.overrunAdd(new byte[] { 1, 2, 3, 4 ... });

// Retrieves and removes data from buffer.
byte[] destination = new byte[10];
int removed = buffer.pop(data);

// Retrieves data without removing from buffer.
byte[] destination = new byte[10];
int retrieved = buffer.peek(destination);

// Check free bytes.
System.out.println("Free bytes: " + buffer.sizeFree());
```

You can also access an interface that allows you to add and remove `short` arrays.

```java
ByteRingBuffer buffer = new ByteRingBuffer(8000);

// The interface of this object is very similar to ByteRingBuffer but uses
// shorts instead of bytes.
ByteRingBuffer.ShortView shortView = buffer.shortView(); 

// Adds shorts to buffer.
int added = shortView.add(new short[] { 1, 2, 3, 4 ... });

// Like add but overwrites existing data when buffer becomes full. 
shortView.overrunAdd(new short[] { 1, 2, 3, 4 ... });

// Retrieves and removes data from buffer.
short[] destination = new short[10];
int removed = shortView.pop(data);

// Retrieves data without removing from buffer.
shortView[] destination = new shortView[10];
int retrieved = shortView.peek(destination);

// Check free shorts.
System.out.println("Free shorts: " + shortView.sizeFree());
```


## Contributing

The easiest way to contribute is by starring this project on GitHub!

https://github.com/daniel-araujo/java-byteringbuffer

If you've found a bug, would like to suggest a feature or need some help, feel free to create an issue on GitHub:

https://github.com/daniel-araujo/java-byteringbuffer/issues


## License

Copyright 2020 Daniel Araujo <contact@daniel-araujo.pt>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
