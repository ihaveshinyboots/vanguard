import csv
import random
import math
from datetime import datetime, timedelta
from decimal import Decimal, ROUND_DOWN

# Define file path
file_path = "game_sales.csv"

# Define constants
num_records = 1_000_000
start_date = datetime(2024, 4, 1)
end_date = datetime(2024, 4, 30)
date_range = (end_date - start_date).days

# Generate CSV data
with open(file_path, mode="w", newline="") as file:
    writer = csv.writer(file)
    # Write header
    writer.writerow(["id", "game_no", "game_name", "game_code", "type", "cost_price", "tax", "sale_price", "date_of_sale"])

    # Write data
    for i in range(1, num_records + 1):
        game_no = random.randint(1, 100)
        game_name = f"Game{game_no}"[:20]  # Ensure max 20 characters
        game_code = f"G{game_no}"[:5]  # Ensure max 5 characters
        game_type = random.choice([1, 2])  # 1 = Online, 2 = Offline

        cost_price = Decimal(str(round(random.uniform(1, 100), 2)))  # Convert to Decimal
        tax = Decimal("0.09")  # Define tax as Decimal

        # Calculate sale_price and round down to 2 decimal places
        sale_price = (cost_price * (1 + tax)).quantize(Decimal("0.01"), rounding=ROUND_DOWN)

        date_of_sale = start_date + timedelta(days=random.randint(0, date_range))

        writer.writerow([i, game_no, game_name, game_code, game_type, cost_price, tax, sale_price, date_of_sale.strftime("%Y-%m-%d %H:%M:%S")])

print(f"CSV file generated: {file_path}")
