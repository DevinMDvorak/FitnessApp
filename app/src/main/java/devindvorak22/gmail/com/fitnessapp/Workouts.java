package devindvorak22.gmail.com.fitnessapp;

public class Workouts {
    Workout[] workouts;

    public Workout[] getWorkouts() {
        return workouts;
    }

    public int getLength() {
        return workouts.length;
    }

    public String getWorkoutName(int index) {
        return workouts[index].getName();
    }

    public int getSetsLength(int index) {
        return workouts[index].getSetsLength();
    }

    public class Workout {
        String name;
        Set[] sets;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSetsLength() {
            return sets.length;
        }

        public Set[] getSets() {
            return sets;
        }

        public class Set {
            String name;
            int time;
            int rest;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getTime() {
                return time;
            }

            public void setTime(int time) {
                this.time = time;
            }

            public int getRest() {
                return rest;
            }

            public void setId(int rest) {
                this.rest = rest;
            }
        }
    }
}