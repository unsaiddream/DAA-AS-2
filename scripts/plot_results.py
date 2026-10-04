"""Create reproducible workload charts from results/results.csv."""

import csv
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


ROOT = Path(__file__).resolve().parents[1]
CSV_PATH = ROOT / "results" / "results.csv"
OUTPUT = ROOT / "results" / "plots"
SIZES = (100, 1_000, 10_000, 100_000)
GROUPS = (
    ("W1", "-", "W1: Random access"),
    ("W2", "-", "W2: Search"),
    ("W3", "head", "W3: Insert and remove at head"),
    ("W3", "middle", "W3: Insert and remove in middle"),
    ("W4", "-", "W4: Priority processing"),
)
COLORS = {
    "DynamicArray": "#2563eb",
    "MyLinkedList": "#dc2626",
    "MinHeap": "#059669",
}


def read_rows():
    with CSV_PATH.open(newline="", encoding="utf-8") as source:
        rows = list(csv.DictReader(source))
    if len(rows) != 36:
        raise ValueError(f"Expected 36 data rows, found {len(rows)}")
    for row in rows:
        row["n"] = int(row["n"])
        row["time_ms"] = float(row["time_ms"])
        for metric in ("steps", "moves", "comparisons"):
            row[metric] = int(row[metric])
    return rows


def style_axis(axis, y_label):
    axis.set_xscale("log")
    axis.set_xticks(SIZES, [f"{size:,}" for size in SIZES])
    axis.set_xlabel("Input size n (elements)")
    axis.set_ylabel(y_label)
    axis.grid(True, color="#e5e7eb", linewidth=0.7)


def plot_group(rows, workload, variant, title):
    selected = [
        row for row in rows
        if row["workload"] == workload and row["variant"] == variant
    ]
    structures = sorted({row["structure"] for row in selected})
    slug = workload.lower() + ("_" + variant if variant != "-" else "")

    figure, axis = plt.subplots(figsize=(8, 5), layout="constrained")
    for structure in structures:
        points = sorted(
            (row for row in selected if row["structure"] == structure),
            key=lambda row: row["n"],
        )
        if [point["n"] for point in points] != list(SIZES):
            raise ValueError(f"Missing sizes for {title}, {structure}")
        axis.plot(
            SIZES, [point["time_ms"] for point in points],
            marker="o", linewidth=2, label=structure, color=COLORS[structure],
        )
    style_axis(axis, "Median time (ms, logarithmic scale)")
    axis.set_yscale("log")
    axis.set_title(title + " — time")
    axis.legend()
    figure.savefig(OUTPUT / f"{slug}_time.png", dpi=180)
    plt.close(figure)

    figure, axes = plt.subplots(1, 3, figsize=(15, 4.5), layout="constrained")
    for axis, metric in zip(axes, ("steps", "moves", "comparisons")):
        all_values = []
        for structure in structures:
            points = sorted(
                (row for row in selected if row["structure"] == structure),
                key=lambda row: row["n"],
            )
            values = [point[metric] for point in points]
            all_values.extend(values)
            axis.plot(
                SIZES, values,
                marker="o", linewidth=2, label=structure, color=COLORS[structure],
            )
        style_axis(axis, metric.capitalize() + " (count)")
        if max(all_values) == 0:
            axis.set_ylim(-0.5, 0.5)
            axis.set_yticks([0])
        else:
            axis.ticklabel_format(axis="y", style="sci", scilimits=(0, 0))
    handles, labels = axes[0].get_legend_handles_labels()
    figure.legend(
        handles, labels, loc="lower center", ncol=len(structures),
        bbox_to_anchor=(0.5, -0.08),
    )
    figure.suptitle(title + " — physical operations")
    figure.savefig(OUTPUT / f"{slug}_metrics.png", dpi=180, bbox_inches="tight")
    plt.close(figure)


def main():
    OUTPUT.mkdir(parents=True, exist_ok=True)
    rows = read_rows()
    for workload, variant, title in GROUPS:
        plot_group(rows, workload, variant, title)
    print(f"Saved 10 PNG charts in {OUTPUT}")


if __name__ == "__main__":
    main()
