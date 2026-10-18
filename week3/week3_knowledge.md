# Lecture 3 — Scheduling

> **Course**: COS40003 Concurrent Programming
> **Topic**: CPU Scheduling — Policies, Trade-offs, and MLFQ

---

## Outline
1. [CPU Scheduling Fundamentals](#cpu-scheduling-fundamentals)
2. [First-Come, First-Served (FIFO)](#first-come-first-served-fifo)
3. [Shortest Job First (non-preemptive)](#shortest-job-first-non-preemptive)
4. [Shortest Job First (with preemption)](#shortest-job-first-with-preemption)
5. [Round-Robin Scheduling](#round-robin-scheduling)
6. [Lottery Scheduling](#lottery-scheduling)
7. [Multi-Level Feedback Queue (MLFQ)](#multi-level-feedback-queue-mlfq)

---

## CPU Scheduling Fundamentals

### Why Scheduling?

The OS must decide **which process runs next** on the CPU. This decision directly impacts system performance and user experience.

### Scheduling Criteria

| Criterion            | Question It Answers                              |
| -------------------- | ------------------------------------------------ |
| **CPU Utilization**  | How busy is the CPU?                             |
| **Turnaround Time**  | How long from job submission to job termination? |
| **Response Time**    | How long does it take to get a first response?   |
| **Missed Deadlines** | Are time-critical tasks completing on time?      |

### Scheduler Options

- **Priorities**: Determine who runs next
  - **Static** — fixed priority assigned when the job starts
  - **Dynamic** — priority altered while the job is in the system (possibly with feedback)
- **Preemptive vs. Non-preemptive**:
  - **Preemptive** — the OS can interrupt a running task at any time to take over
  - **Non-preemptive** — once a task starts, it runs until completion or voluntarily yields

### Key Metrics

$$T_{\text{turnaround}} = T_{\text{completion}} - T_{\text{arrival}}$$

$$\text{Delay} = T_{\text{turnaround}} - T_{\text{processing}}$$

---

## First-Come, First-Served (FIFO)

> Start jobs in the order they arrive. Run each job until completion.

- **Non-preemptive**: once a job starts, it runs to completion
- Simple to implement — just a FIFO queue
- Also known as: **First-In, First-Out (FIFO)**

### Example Walkthrough

| Process | Arrival Time | Processing Time | Delay | Turnaround Time |
|---|---|---|---|---|
| 1 | 0 | 3 | 0 | 3 |
| 2 | 2 | 6 | 1 | 7 |
| 3 | 4 | 4 | 5 | 9 |
| 4 | 6 | 5 | 7 | 12 |
| 5 | 8 | 2 | 10 | 12 |

**Total delay: 23**

### Gantt Chart

```
┌──────┬────────────┬────────┬──────────┬────┐
│  P1  │     P2     │   P3   │    P4    │ P5 │
└──────┴────────────┴────────┴──────────┴────┘
0      3            9        13         18   20
```

> [!WARNING] Convoy Effect
> Short jobs stuck behind long jobs suffer excessive wait times. A single long-running job can inflate the delay for all subsequent jobs.

---

## Shortest Job First (SJF)

> Select the job with the **shortest (expected) running time**. Non-preemptive.

- When a job finishes, the scheduler picks the shortest job **from those currently waiting**
- Optimal for minimizing **average turnaround time** when all jobs arrive at the same time

### Example Walkthrough (Same Job Mix)

| Process | Arrival Time | Processing Time | Delay | Turnaround Time |
|---|---|---|---|---|
| 1 | 0 | 3 | 0 | 3 |
| 2 | 2 | 6 | 1 | 7 |
| 3 | 4 | 4 | 7 | 11 |
| 4 | 6 | 5 | 9 | 14 |
| 5 | 8 | 2 | 1 | 3 |

**Total delay: 18** (improved from FIFO's 23)

### Gantt Chart

```
┌──────┬────────────┬────┬────────┬──────────┐
│  P1  │     P2     │ P5 │   P3   │    P4    │
└──────┴────────────┴────┴────────┴──────────┘
0      3            9    11       15         20
```

> [!NOTE]
> After P1 finishes at t=3, only P2 has arrived — so P2 runs. After P2 finishes at t=9, P3 (4), P4 (5), and P5 (2) are waiting — P5 is shortest, so it runs next.

> [!WARNING]
> SJF requires **knowing job lengths in advance** — often impractical in real systems.

---

## Shortest Remaining Time (Preemptive SJF)

> **Preemptive** version of SJF: whenever a **new job arrives**, compare its total time with the **remaining time** of the currently running job. If the new job is shorter, preempt.

- Also known as: **Preemptive Shortest Job First (PSJF)** or **Shortest Remaining Time First (SRTF)**

### Example Walkthrough (Same Job Mix)

| Process | Arrival Time | Processing Time | Delay | Turnaround Time |
|---|---|---|---|---|
| 1 | 0 | 3 | 0 | 3 |
| 2 | 2 | 6 | 7 | 13 |
| 3 | 4 | 4 | 0 | 4 |
| 4 | 6 | 5 | 9 | 14 |
| 5 | 8 | 2 | 0 | 2 |

**Total delay: 16** (improved from SJF's 18)

### Gantt Chart

```
┌──────┬──┬────────┬────┬──────────┬──────────┐
│  P1  │P2│   P3   │ P5 │    P2    │    P4    │
└──────┴──┴────────┴────┴──────────┴──────────┘
0      3  4        8    10         15         20
```

> [!TIP]
> Preemptive SJF achieves the **lowest total delay** among the three algorithms so far (FIFO: 23, SJF: 18, PSJF: 16), but at the cost of more context switches and requiring knowledge of job durations.

---

## Round-Robin Scheduling

> **Goal**: Enable interactivity by limiting the amount of CPU time a process can have at once.

### Key Concept: Time Quantum (Time Slice)

- The amount of time the OS gives a process before **preempting** it
- Typical range: **1 to 100 ms**
- Each process runs for at most one time quantum, then goes to the **back of the ready queue**

### Example Walkthrough (Time Quantum = 1)

| Process | Arrival Time | Processing Time | Delay | Turnaround Time |
|---|---|---|---|---|
| 1 | 0 | 3 | 1 | 4 |
| 2 | 2 | 6 | 10 | 16 |
| 3 | 4 | 4 | 9 | 13 |
| 4 | 6 | 5 | 9 | 14 |
| 5 | 8 | 2 | 5 | 7 |

**Total delay: 34** (worse turnaround than FIFO!)

### Gantt Chart

```
┌────┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬────┐
│ P1 │P2│P1│P2│P3│P2│P3│P4│P2│P5│P3│P4│P3│P5│P2│P4│P2│ P4 │
└────┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴────┘
0    2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18   20
```

### Trade-offs

| Factor | Effect |
|---|---|
| **More jobs** | Longer time between scheduling of each job → slower responses |
| **Larger time quantum** | Longer gap between turns → slower responses (approaches FIFO) |
| **Smaller time quantum** | Better response time ↑ but more context-switch overhead ↑ |

> [!IMPORTANT]
> Round-Robin optimizes for **response time** (fairness, interactivity) but suffers in **turnaround time**. It is the opposite trade-off from SJF.

---

## Lottery Scheduling

> A kind of **proportional share** scheduling.

### How It Works
1. The scheduler gives each task some **lottery tickets**
2. To select the next process to run, the scheduler **randomly picks a winning ticket number**
3. The process holding the winning ticket gets to run

### Example

| Task | Tickets | Expected CPU Share |
|---|---|---|
| A | 50 | 50% |
| B | 15 | 15% |
| C | 35 | 35% |
| **Total** | **100** | **100%** |

> [!NOTE]
> Over a long period, each task receives CPU time roughly proportional to its ticket count. More tickets = higher probability of being selected = more CPU time.

---

## Multi-Level Feedback Queue (MLFQ)

> Invented by **Fernando José Corbató** (Turing Award 1990) — *"for his pioneering work in organizing the concepts and leading the development of the general-purpose, large-scale, time-sharing and resource-sharing computer systems"*

### Aim

Achieve **both** goals simultaneously:
1. **Optimize turnaround time** — SJF/PSJF is good, but we don't know job lengths
2. **Minimize response time** — RR is good for response, but terrible for turnaround

### MLFQ Basics

Tasks are placed in **different queues** with **different priority levels**.

```
┌──────────────────┐  Highest Priority
│    Queue Q2      │  ← New jobs start here
├──────────────────┤
│    Queue Q1      │
├──────────────────┤
│    Queue Q0      │  ← Long-running jobs end up here
└──────────────────┘  Lowest Priority
```

---

### The Five Rules of MLFQ

| Rule | Description |
|---|---|
| **Rule 1** | If Priority(A) > Priority(B), A runs (B doesn't) |
| **Rule 2** | If Priority(A) = Priority(B), A & B run in **Round-Robin** |
| **Rule 3** | When a job **enters the system**, it is placed at the **highest priority** (topmost queue) |
| **Rule 4** | Once a job **uses up its time allotment** at a given level (regardless of how many times it has given up the CPU), its priority is **reduced** (moves down one queue) |
| **Rule 5** | After some **time period S**, move **all jobs** to the topmost queue (**Priority Boost**) |

> [!NOTE]
> Rules 4a and 4b were the original formulation:
> - **4a**: If a job uses up an entire time slice → move down
> - **4b**: If a job gives up CPU before time slice is up → stay at same level
>
> These were later replaced by the unified **Rule 4** (tracking total allotment) to prevent gaming.

---

### How MLFQ Approximates SJF

1. A new job is **assumed to be short** → placed at highest priority
2. If it **actually is short** → it completes quickly at high priority ✓
3. If it **is actually long** → it slowly moves down the queues, proving itself to be a long-running job

> MLFQ **learns** job behavior through observation rather than requiring advance knowledge.

---

### MLFQ Examples

#### Example 1: A Single Long-Running Job
- Job enters at highest priority (Q2) — Rule 3
- After 10ms, moves to Q1 — Rule 4
- After another 10ms, moves to Q0 — Rule 4
- Stays at Q0 for the remainder of its execution

#### Example 2: Along Came a Short Job
- Long job A is running at Q0
- Short job B (20ms) arrives → enters at Q2 (Rule 3)
- B completes before reaching bottom queue (within two time slices)
- B gets **fast turnaround** despite A being in the system

#### Example 3: What About I/O?
- Interactive job B does lots of I/O (e.g., waiting for keyboard/mouse input)
- B only needs CPU for ~1ms before performing I/O
- Under Rule 4b (original), B stays at high priority because it gives up CPU voluntarily
- This is **desirable**: interactive jobs should remain responsive

---

### Problems with Basic MLFQ

#### 1. Starvation
- Too many interactive jobs → they consume **all** CPU time
- Long-running jobs **never** receive CPU time

#### 2. Gaming the Scheduler
- A malicious job issues a fake I/O operation **just before** the time slice expires
- This keeps it at high priority while getting maximum CPU time
- Exploits Rule 4b

### Solutions

| Problem | Solution | Mechanism |
|---|---|---|
| **Starvation** | **Priority Boost** (Rule 5) | After time period S, move all jobs to topmost queue |
| **Gaming** | **Better Accounting** (Rule 4) | Track **total** time allotment at each level, regardless of how many bursts |

---

### Tuning MLFQ

Key parameters with **no easy answers**:
- How many queues?
- How big should the time slice be per queue?
- How often should priority be boosted (period S)?

#### Widely Accepted Practice
- **High-priority queues** → **short** time slices (favor interactive responsiveness)
- **Low-priority queues** → **longer** time slices (reduce overhead for batch jobs)

---

### Why MLFQ Is Good

- Does **not** require prior knowledge of job lengths
- **Observes** job behavior and prioritizes accordingly
- **Excellent performance** for short-running interactive jobs (similar to SJF/PSJF)
- **Fair** to long-running CPU-intensive workloads
- Used in real operating systems: **BSD Unix**, **Solaris**, **Windows**

---

## Scheduling Algorithms Comparison

| Algorithm | Type | Optimizes | Weakness | Total Delay (Example) |
|---|---|---|---|---|
| **FIFO** | Non-preemptive | Simplicity | Convoy effect | 23 |
| **SJF** | Non-preemptive | Turnaround time | Requires knowing job length | 18 |
| **PSJF/SRT** | Preemptive | Turnaround time | Requires knowing job length; starvation of long jobs | 16 |
| **Round-Robin** | Preemptive | Response time / fairness | Poor turnaround time | 34 |
| **Lottery** | Preemptive | Proportional fairness | Non-deterministic; no hard guarantees | — |
| **MLFQ** | Preemptive | Both turnaround & response | Complex tuning | — |

> [!IMPORTANT] Key Takeaway
> - **Turnaround time** and **response time** are often at odds — no single algorithm optimizes both perfectly
> - **MLFQ** is the most practical real-world scheduler because it **adapts** to job behavior without requiring advance knowledge
> - The fundamental insight: **observe and react** rather than **predict and plan**

---

## References
- *Operating Systems: Three Easy Pieces* — Chapters 7–9
- Intro to Operating Systems — Portland State University (Jonathan Walpole)

---

#concurrent-programming #scheduling #FIFO #SJF #round-robin #lottery #MLFQ #week3
