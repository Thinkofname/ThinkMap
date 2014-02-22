part of map_viewer;

/*
  Temp issue tracker until I can be bothered to switch to a proper one which
  will happen once this nears release

  (#1)  TODO: Refactor WebGL renderer into multiple files
  (#2)  TODO: Fix the whole world being backward and just being flipped back with shaders/CW cullface
  (#3)  TODO: Model format for special blocks (Blocked on #2)
  (#4)   -THINK: Load separately or merge at compile time
  (#5)  TODO: Clean up block registry
  (#6)   -THINK: Maybe load from a json file and have an editor for it
  (#7)  TODO: Finish implementing all blocks
  (#8)  THINK: Maybe use the unlimited blocks that the registry provides to work out block shape at placement time
  (#9)   -THINK: May have too big of a performance on chunk load plus the hit from updating surrounding chunks
  (#10) TODO: Documentation - Comment all the things!
  (#11) TODO: Clean up the Build/Load queue system
  (#12) TODO: Editor for models (Blocked on #3)
  (#13) TODO: Editor for registry (Requires #6)
  (#14) TODO_LOW: Sign Text
  (#15) TODO_LOW: Entities
  (#16) TODO_LOW: Players (Most likely will require #15)
  (#17) TODO_LOW: Slow down/change camera to work better with the current world loading speed
  (#18) TODO_LOW: Add a fly mode to first person
  (#19) TODO_LOW: User interface
  (#20) TODO_LOW: Multiple world support
  (#21)  -THINK: Maybe let portals work
  (#22) TODO_LOW: Tidy up browser prefixing
  (#23) TODO_LOW: Re-add canvas isometric renderer
  (#24)  -THINK: May not be worth it if WebGL support is good enough
  (#25) THINK: Move chunk conversion to server side to ease load on the client (Might not be worth the server perf hit)
*/