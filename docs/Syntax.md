|                               | Terms           | Types                    |
| ----------------------------- | --------------- | ------------------------ |
| Integers	                    | (e.g.) 2	      | int                      |
| Linear Integers	            | (e.g.) 2l	      | linint                   |
| Booleans	                    | (e.g.) true	  | bool                     |
| Linear Booleans	            | (e.g.) truel	  | linbool                  |
| Strings	                    | (e.g.) “abc”	  | string                   |
| Unit	                        | ()	          | ()                       |
| Unrestricted Abstraction	    | fn x:A => {M}	  | A -> B                   |
| Linear Abstraction	        | lfn x:A =o> {M} |	A -o> B                  |
| Dep. Unrestricted Abstraction | fn x:A => {M}	  | (x:A) -> B               |
| Dep. Linear Abstraction	    | lfn x:A =o> {M} | (x:A) -o> B              |
| Unrestricted Pairs	        | (a, b)	      | (A, B)                   |
| Linear Pairs	                | (a \| b)	      | (A \| B)                 |
| Dep. Unrestricted Pairs	    | (a, b)	      | (x:A, B)                 |
| Dep. Linear Pairs	            | (a \| b)	      | (x:A \| B)               |
| Unrestricted Union	        | #label(M)	      | union {#l1:A, #l2: B}    |
| Linear Union	                | $label(M)	      | linunion {$l1:A, $l2: B} |
| Equality Types                |                 | (M = N : A)              |
